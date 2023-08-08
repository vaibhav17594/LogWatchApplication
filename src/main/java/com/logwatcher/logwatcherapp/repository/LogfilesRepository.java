package com.logwatcher.logwatcherapp.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LogfilesRepository implements Repository {

    private Map<String, Object> fileDirectory;

    @Autowired
    public LogfilesRepository() {
        this.fileDirectory = new HashMap<>();
    }

    @Override
    public boolean addFile(String logFile, Object metadata) {
        fileDirectory.put(logFile, metadata);
        return true;
    }

    @Override
    public boolean removeFile(String logFile) {
        fileDirectory.remove(logFile);
        return true;
    }

    @Override
    public boolean isPresent(String logfile) {
        return fileDirectory.containsKey(logfile);
    }
}
