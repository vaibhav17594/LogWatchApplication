package com.logwatcher.logwatcherapp.service;

import com.logwatcher.logwatcherapp.entities.LogWatchRequest;
import com.logwatcher.logwatcherapp.entities.event.Event;
import com.logwatcher.logwatcherapp.exception.InvalidLogFileException;
import com.logwatcher.logwatcherapp.repository.LogfilesRepository;
import com.logwatcher.logwatcherapp.util.LogWatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class LogWatcherOrchestrator {

    private Map<String, LogWatcher> logWatcherMap;
    private SessionService sessionService;
    private LogProcessorService logProcessorService;
    private LogfilesRepository logfilesRepository;
    private ReadWriteLock lock;
    private Lock readLock;
    private Lock writeLock;

    @Autowired
    public LogWatcherOrchestrator(LogProcessorService logProcessorService, SessionService sessionService, LogfilesRepository logfilesRepository) {
        this.sessionService = sessionService;
        this.logProcessorService = logProcessorService;
        this.logWatcherMap = new HashMap<>();
        this.logfilesRepository = logfilesRepository;
        this.lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }

    public void registerLogWatcher(LogWatchRequest logWatchRequest) throws InvalidLogFileException {
        if (!logfilesRepository.isPresent(logWatchRequest.getLogfile())) {
            throw new InvalidLogFileException("File now does not exist in repository! " + logWatchRequest.getLogfile());
        }
        try {
            writeLock.lock();
            if (!logWatcherMap.containsKey(logWatchRequest.getLogfile())) {
                LogWatcher logWatcher = new LogWatcher(logProcessorService, logWatchRequest.getLogfile());
                logWatcherMap.put(logWatchRequest.getLogfile(), logWatcher);
            }
        } catch (Exception exception) {
            //
        } finally {
            writeLock.unlock();
        }
    }

    public void unregisterLogWatcherIfRequired(LogWatchRequest logWatchRequest) {
        Set<String> sessions = sessionService.getSessionsForLogFile(logWatchRequest.getLogfile());
        if (sessions.isEmpty()) {
            try {
                writeLock.lock();
                LogWatcher logWatcher = logWatcherMap.get(logWatchRequest.getLogfile());
                if (logWatcher != null) {
                    logWatcher.stop();
                }
                logWatcherMap.remove(logWatchRequest.getLogfile());
            } catch (Exception exception) {
                //Log.
            } finally {
                writeLock.unlock();
            }
        }
    }

    public Set<Event> getAllLogEvents(String logfile) {
        try {
            readLock.lock();
            LogWatcher logWatcher = logWatcherMap.get(logfile);
            if (logWatcher != null) {
                return logWatcher.getAllLogEvents();
            } else {
                return new TreeSet<>();
            }
        } catch (Exception exception) {
            //Log.
            return new TreeSet<>();
        } finally {
            readLock.unlock();
        }
    }
}
