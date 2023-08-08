package com.logwatcher.logwatcherapp.controller;

import com.logwatcher.logwatcherapp.entities.LogWatchRequest;
import com.logwatcher.logwatcherapp.entities.event.Event;
import com.logwatcher.logwatcherapp.entities.event.LogEvent;
import com.logwatcher.logwatcherapp.exception.InvalidLogFileException;
import com.logwatcher.logwatcherapp.service.LogWatcherOrchestrator;
import com.logwatcher.logwatcherapp.service.NotificationService;
import com.logwatcher.logwatcherapp.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.Set;

@Controller
public class LogWatcherController {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private LogWatcherOrchestrator logWatcherOrchestrator;

    @MessageMapping("/subscribe")
    public void subscribe(SimpMessageHeaderAccessor simpMessageHeaderAccessor, LogWatchRequest logWatchRequest) {

        String sessionId = simpMessageHeaderAccessor.getUser().getName();
        String logFile = logWatchRequest.getLogfile();

        try {
            logWatcherOrchestrator.registerLogWatcher(logWatchRequest);
        } catch (InvalidLogFileException invalidLogFileException) {
            notificationService.sendEventsToUser(sessionId, Collections.singleton(buildInvalidLogFileException()));
            System.out.println("ERROR: " + invalidLogFileException.getMessage());
            return;
        }

        Set<Event> logEvents = logWatcherOrchestrator.getAllLogEvents(logWatchRequest.getLogfile());
        notificationService.sendEventsToUser(sessionId, logEvents);
        sessionService.registerSession(logFile, sessionId);
    }

    @MessageMapping("/unsubscribe")
    public void unsubscribe(SimpMessageHeaderAccessor simpMessageHeaderAccessor, LogWatchRequest logWatchRequest) {

        String sessionId = simpMessageHeaderAccessor.getUser().getName();
        String logFile = logWatchRequest.getLogfile();
        sessionService.unregisterSession(logFile, sessionId);
        logWatcherOrchestrator.unregisterLogWatcherIfRequired(logWatchRequest);
    }

    private LogEvent buildInvalidLogFileException() {
        return LogEvent.builder().logfile("Invalid").content("Invalid log file!").build();
    }
}
