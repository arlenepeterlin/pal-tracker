package io.pivotal.pal.tracker;

import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    private long counter = 1L;
    private Map<Long,TimeEntry> timeEntries = new HashMap<>();

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        timeEntry.setId(counter);
        timeEntries.put(counter,timeEntry);
        TimeEntry result = timeEntries.get(counter);
        counter++;
        return result;
    }

    @Override
    public TimeEntry find(long id) {
        return timeEntries.get(id);
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        timeEntry.setId(id);
        timeEntries.replace(id, timeEntry);
        return timeEntries.get(id);
    }

    @Override
    public void delete(long id) {
        timeEntries.remove(id);
    }

    @Override
    public List<TimeEntry> list() {
        List<TimeEntry> result = new ArrayList<>();

        for(Map.Entry entry : timeEntries.entrySet()) {
            result.add((TimeEntry) entry.getValue());
        }

        return result;
    }

}
