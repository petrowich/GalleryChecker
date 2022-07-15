package org.petrowich.gallerychecker.utils;

import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

public class QuartzTriggerHelper {
    private static final String TRIGGER_GROUP = "fetching-galleries-triggers";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private SimpleTrigger buildTriggerAtOnce(JobDetail jobDetail) {
        SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withMisfireHandlingInstructionFireNow();

        String triggerId = jobDetail.getKey().toString();

        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(triggerId, TRIGGER_GROUP)
                .withDescription("Fetching galleries at once")
                .startNow()
                .withSchedule(simpleScheduleBuilder)
                .build();
    }

    private SimpleTrigger buildTriggerAtTime(JobDetail jobDetail, ZonedDateTime startAt) {
        SimpleScheduleBuilder simpleScheduleBuilder = simpleSchedule().withMisfireHandlingInstructionFireNow();

        String triggerId = generateId();

        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(triggerId, TRIGGER_GROUP)
                .withDescription(String.format("Fetching galleries at %s", startAt.format(dateTimeFormatter)))
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(simpleScheduleBuilder)
                .build();
    }

    private SimpleTrigger buildTriggerAtTimeWithIntervalInHours(JobDetail jobDetail,
                                                                ZonedDateTime startAt,
                                                                Integer intervalInHours) {
        SimpleScheduleBuilder simpleScheduleBuilder = simpleSchedule()
                .withIntervalInHours(intervalInHours)
                .repeatForever();

        String triggerId = generateId();

        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(triggerId, TRIGGER_GROUP)
                .withDescription(String.format("Fetching galleries at %s", startAt.format(dateTimeFormatter)))
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(simpleScheduleBuilder)
                .build();
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }
}
