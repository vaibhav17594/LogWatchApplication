package com.logwatcher.logwatcherapp.entities.event;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class Event implements Comparable<Event> {
    private String content;
    private Long timestamp;

    @Override
    public int compareTo(Event o) {
        int compareResult = this.getTimestamp().compareTo(o.getTimestamp());
        if (compareResult != 0) {
            return compareResult;
        } else {
            return this.getContent().compareTo(o.getContent());
        }
    }
}
