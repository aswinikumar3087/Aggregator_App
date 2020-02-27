package com.bt.metb.aggregator.scheduledJobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.Instant;
import java.util.Date;

import static java.time.temporal.ChronoUnit.SECONDS;

@Configuration
@EnableScheduling
@RefreshScope
public class SchedulingConfiguration implements SchedulingConfigurer {

    @Autowired
    private StartStuckRequestPoller startStuckRequestPoller;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(
                () -> startStuckRequestPoller.execute(),
                triggerContext -> {
                    Instant nextTriggerTime = Instant.now().
                            plus(startStuckRequestPoller.getDelay(), SECONDS);
                    return Date.from(nextTriggerTime);
                }
        );
    }
}