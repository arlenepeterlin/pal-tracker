package io.pivotal.pal.tracker;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.lang.String;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

@Repository
public class JdbcTimeEntryRepository implements TimeEntryRepository {

    private JdbcTemplate template;
    private KeyHolder keyHolder = new GeneratedKeyHolder();



    public JdbcTimeEntryRepository(DataSource datasource){
        template = new JdbcTemplate();
        template.setDataSource(datasource);


//                 id         BIGINT(20) NOT NULL AUTO_INCREMENT,
//                project_id BIGINT(20),
//                user_id    BIGINT(20),
//                date       DATE,
//                hours      INT,
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {

        String insertSql = "insert into time_entries (project_id, user_id, date, hours) values (?,?,?,?)";

        template.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(insertSql, RETURN_GENERATED_KEYS);
            ps.setLong(1, timeEntry.getProjectId());
            ps.setLong(2, timeEntry.getUserId());
            ps.setDate(3, Date.valueOf(timeEntry.getDate()));
            ps.setInt(4,timeEntry.getHours());
            return ps;
        }, keyHolder);


        return find(keyHolder.getKey().longValue());

    }

    @Override
    public TimeEntry find(long id) {
        String findSql = "select id, project_id, user_id, date, hours from time_entries where id=(?)";

        return template.query(findSql, new Object[]{id}, extractor);
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        String updateSql = "update time_entries set project_id=?, user_id=?, date=?, hours=? where id=?";

        template.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(updateSql);
            ps.setLong(1, timeEntry.getProjectId());
            ps.setLong(2, timeEntry.getUserId());
            ps.setDate(3, Date.valueOf(timeEntry.getDate()));
            ps.setInt(4,timeEntry.getHours());
            ps.setLong(5, id);
            return ps;
        });


        return find(id);
    }

    @Override
    public void delete(long id) {
        this.template.update("delete from time_entries where id=?", new Object[]{id});

    }

    @Override
    public List<TimeEntry> list() {
        String listSql = "select id, project_id, user_id, date, hours from time_entries";

        return template.query(listSql,mapper);
    }

    private final RowMapper<TimeEntry> mapper = (rs, rowNum) -> new TimeEntry(
            rs.getLong("id"),
            rs.getLong("project_id"),
            rs.getLong("user_id"),
            rs.getDate("date").toLocalDate(),
            rs.getInt("hours")
    );

    private final ResultSetExtractor<TimeEntry> extractor =
            (rs) -> rs.next() ? mapper.mapRow(rs, 1) : null;
}
