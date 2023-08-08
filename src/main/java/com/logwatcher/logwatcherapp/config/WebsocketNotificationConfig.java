package com.logwatcher.logwatcherapp.config;

import com.logwatcher.logwatcherapp.service.LogProcessorService;
import com.logwatcher.logwatcherapp.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebsocketNotificationConfig {

    @Autowired
    public WebsocketNotificationConfig(LogProcessorService logProcessorService, NotificationService notificationService) {
        logProcessorService.addObserver(notificationService);
    }
}
