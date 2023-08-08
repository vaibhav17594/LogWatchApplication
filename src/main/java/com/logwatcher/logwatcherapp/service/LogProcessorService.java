package com.logwatcher.logwatcherapp.service;

import com.logwatcher.logwatcherapp.entities.event.LogEvent;
import org.springframework.stereotype.Service;

import java.util.Observable;

@Service
public class LogProcessorService extends Observable {

    public void processLog(LogEvent logEvent) {
        setChanged();
        notifyObservers(logEvent);
    }
}
