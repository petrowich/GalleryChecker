package org.petrowich.gallerychecker.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.petrowich.gallerychecker.models.master.JobOwner;
import org.petrowich.gallerychecker.scheduler.exceptions.JobException;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@Slf4j
public abstract class SchedulerService {
    private static final String USERNAME = "username";
    private static final String HOST = "host";
    private static final String AIM = "aim";
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT);
    private static final ZoneId zoneId = ZoneId.systemDefault();
    private final JobType jobType;
    private final JobRepository jobRepository;
    private final Scheduler scheduler;

    protected SchedulerService(JobType jobType, JobRepository jobRepository, Scheduler scheduler) {
        this.jobType = jobType;
        this.jobRepository = jobRepository;
        this.scheduler = scheduler;
    }

    public List<SimpleTrigger> getAllTriggers() {
        log.debug("Getting all triggers by {} group", jobType);
        GroupMatcher<TriggerKey> groupMatcher = GroupMatcher.groupEquals(getTriggerGroup());
        try {
            return scheduler.getTriggerKeys(groupMatcher).stream()
                    .map(this::getTriggerByKey)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        } catch (SchedulerException exception) {
            log.error(exception.getMessage(), exception);
            return emptyList();
        }
    }

    public Optional<SimpleTrigger> getTriggerById(String triggerId) {
        log.debug("Getting trigger by triggerId={} - {}", triggerId, jobType);
        TriggerKey triggerKey = new TriggerKey(triggerId, getTriggerGroup());
        return getTriggerByKey(triggerKey);
    }

    public Optional<SimpleTrigger> getTriggerByKey(TriggerKey triggerKey) {
        log.debug("Getting trigger by triggerKey={} - {}", triggerKey, jobType);
        try {
            return Optional.of((SimpleTrigger) scheduler.getTrigger(triggerKey));
        } catch (SchedulerException exception) {
            log.error(exception.getMessage(), exception);
        }
        return Optional.empty();
    }

    public String getTriggerStateName(Trigger trigger) {
        log.debug("Getting trigger state of {}", trigger);
        try {
            return scheduler.getTriggerState(trigger.getKey()).name();
        } catch (SchedulerException exception) {
            log.error(exception.getMessage(), exception);
            return "not defined";
        }
    }

    public void cancelTaskByTrigger(SimpleTrigger trigger) {
        log.debug("Canceling job {}", trigger);
        try {
            scheduler.unscheduleJob(trigger.getKey());
        } catch (SchedulerException exception) {
            log.error(exception.getMessage(), exception);
        }
    }

    public void interruptTaskByTrigger(SimpleTrigger trigger) {
        log.debug("Interrupting job {}", trigger);
        try {
            Optional<JobExecutionContext> optionalJobExecutionContext =
                    scheduler.getCurrentlyExecutingJobs().stream()
                            .filter(jobExecutionContext ->
                                    jobExecutionContext.getTrigger().getKey().equals(trigger.getKey()))
                            .findAny();
            if (optionalJobExecutionContext.isPresent()) {
                String fireInstanceId = optionalJobExecutionContext.get().getFireInstanceId();
                scheduler.interrupt(fireInstanceId);
            } else {
                log.error("Job is not fired!");
            }
        } catch (SchedulerException exception) {
            log.error(exception.getMessage(), exception);
        }
    }

    public void scheduleJobAtOnce(JobOwner jobOwner, Integer jobAim, String username) throws JobException {
        log.info("Scheduling {} job of {} at once.", jobType.getJobDescription(), jobOwner);
        JobDetail jobDetail = buildJobDetail(jobOwner, jobAim, username);
        SimpleTrigger trigger = buildTriggerForAtOnce(jobDetail);
        scheduleJob(jobDetail, trigger, jobOwner);
        log.info("New {} job of {} at once scheduled successfully!", jobType.getJobDescription(), jobOwner);
    }

    public void scheduleJobAtTimeWitPeriod(JobOwner jobOwner,
                                           Integer jobAim,
                                           LocalDateTime startAt,
                                           Integer period,
                                           String username) throws JobException {
        log.info("Scheduling {} job of {} at {} with interval in {} {}.",
                jobType.getJobDescription(),
                jobOwner,
                startAt.format(dateTimeFormatter),
                period,
                jobType.getJobPeriodUnit());

        JobDetail jobDetail = buildJobDetail(jobOwner, jobAim, username);
        Trigger trigger = buildTriggerWithInterval(jobDetail, startAt, period);
        scheduleJob(jobDetail, trigger, jobOwner);

        log.info("New {} job of {} at {} with interval in {} {} scheduled successfully!",
                jobType.getJobDescription(),
                jobOwner,
                startAt.format(dateTimeFormatter),
                period,
                jobType.getJobPeriodUnit());
    }

    private JobDetail buildJobDetail(JobOwner jobOwner, Integer jobAim, String username) throws JobException {
        String jobId = generateId();

        Class<? extends Job> jobClass = jobRepository.getJobClass(jobOwner);

        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobId, getJobGroup())
                .usingJobData(HOST, jobOwner.getHost())
                .withDescription(String.format("%s of %s", jobType.getJobDescription(), jobOwner))
                .storeDurably()
                .build();

        jobDetail.getJobDataMap().put(USERNAME, username);
        jobDetail.getJobDataMap().put(AIM, jobAim);

        return jobDetail;
    }

    private SimpleTrigger buildTriggerForAtOnce(JobDetail jobDetail) {
        SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withMisfireHandlingInstructionFireNow();

        String triggerId = generateId();

        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .usingJobData(jobDetail.getJobDataMap())
                .withIdentity(triggerId, getTriggerGroup())
                .withDescription(String.format("%s at once", jobType.getJobDescription()))
                .startNow()
                .withSchedule(simpleScheduleBuilder)
                .build();
    }

    private SimpleTrigger buildTriggerWithInterval(JobDetail jobDetail,
                                                   LocalDateTime startAt,
                                                   Integer interval) {
        int intervalInHours = interval * jobType.getJobPeriodRate();
        SimpleScheduleBuilder simpleScheduleBuilder = simpleSchedule()
                .withIntervalInHours(intervalInHours)
                .repeatForever();

        String triggerId = generateId();

        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .usingJobData(jobDetail.getJobDataMap())
                .withIdentity(triggerId, getTriggerGroup())
                .withDescription(String.format("%s at %s",
                        jobType.getJobDescription(), startAt.format(dateTimeFormatter)))
                .startAt(Date.from(startAt.atZone(zoneId).toInstant()))
                .withSchedule(simpleScheduleBuilder)
                .build();
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }

    private void scheduleJob(JobDetail jobDetail, Trigger trigger, JobOwner jobOwner) throws JobException {
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException exception) {
            log.error(exception.getMessage(), exception);
            throw new JobException(exception.getMessage(), jobOwner, exception);
        }
    }

    private String getJobGroup() {
        return String.format("%s-jobs", jobType.getJobName());
    }

    private String getTriggerGroup() {
        return String.format("%s-triggers", jobType.getJobName());
    }
}
