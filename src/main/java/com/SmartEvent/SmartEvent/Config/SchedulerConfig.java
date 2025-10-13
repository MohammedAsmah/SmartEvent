package com.SmartEvent.SmartEvent.Config;

import com.SmartEvent.SmartEvent.Service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Autowired
    private EventService eventService;

    @Scheduled(cron = "0 0 0 * * *") // every day at midnight
    public void archiveOldEvents() {
        eventService.archivePastEvents();
        System.out.println("✅ Archived old events automatically");
    }
}