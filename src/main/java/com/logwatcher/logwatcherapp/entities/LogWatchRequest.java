package com.logwatcher.logwatcherapp.entities;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class LogWatchRequest {
    private String logfile;

    public LogWatchRequest() {
    }
}
