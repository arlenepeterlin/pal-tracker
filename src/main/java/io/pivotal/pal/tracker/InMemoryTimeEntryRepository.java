package io.pivotal.pal.tracker;

import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    private AtomicLong counter = new AtomicLong(1);
    private Map<Long,TimeEntry> timeEntries = new ConcurrentHashMap<>();

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        timeEntry.setId(counter.getAndIncrement());
        timeEntries.put(timeEntry.getId(),timeEntry);
        TimeEntry result = timeEntries.get(timeEntry.getId());
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
