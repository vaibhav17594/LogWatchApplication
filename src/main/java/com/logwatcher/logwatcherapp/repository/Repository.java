package com.logwatcher.logwatcherapp.repository;

public interface Repository {
    public boolean addFile(String logfile, Object metadata);
    public boolean removeFile(String logfile);
    public boolean isPresent(String logfile);
}
