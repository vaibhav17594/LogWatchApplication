package com.logwatcher.logwatcherapp.service;

import com.logwatcher.logwatcherapp.entities.event.Event;
import com.logwatcher.logwatcherapp.entities.event.LogEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

@Service
public class NotificationService implements Observer {

    private SessionService sessionService;
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationService(SessionService sessionService, SimpMessagingTemplate messagingTemplate) {
        this.sessionService = sessionService;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void update(Observable o, Object logWatchEvent) {
        sendEventToAllUsersOfLogFile(((LogEvent) logWatchEvent).getLogfile(), Collections.singleton(((LogEvent) logWatchEvent)));
    }

    public void sendEventsToUser(String sessionId, Set<Event> events) {
        messagingTemplate.convertAndSendToUser(sessionId, "/queue/logs", events);
    }

    private void sendEventToAllUsersOfLogFile(String logfile, Set<Event> events) {
        sessionService.getSessionsForLogFile(logfile).forEach(sessionId -> {
            messagingTemplate.convertAndSendToUser(sessionId, "/queue/logs", events);
        });
    }
}
