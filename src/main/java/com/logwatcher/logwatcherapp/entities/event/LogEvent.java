package com.logwatcher.logwatcherapp.entities.event;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class LogEvent extends Event {

    private String logfile;
}
