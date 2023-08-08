package com.logwatcher.logwatcherapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Service
public class SessionService {

    private Map<String, TreeSet<String>> sessionsMap;

    @Autowired
    public SessionService() {
        this.sessionsMap = new HashMap<>();
    }

    public boolean registerSession(String logfile, String sessionId) {

        TreeSet<String> sessionsForLogFile = sessionsMap.getOrDefault(logfile, new TreeSet<>());
        sessionsForLogFile.add(sessionId);
        sessionsMap.put(logfile, sessionsForLogFile);

        return true;
    }

    public boolean unregisterSession(String logfile, String sessionId) {
        TreeSet<String> sessionsForLogFile = sessionsMap.getOrDefault(logfile, new TreeSet<>());
        sessionsForLogFile.remove(sessionId);
        sessionsMap.put(logfile, sessionsForLogFile);

        return true;
    }

    public Set<String> getSessionsForLogFile(String logfile) {
        return sessionsMap.getOrDefault(logfile, new TreeSet<>());
    }
}
