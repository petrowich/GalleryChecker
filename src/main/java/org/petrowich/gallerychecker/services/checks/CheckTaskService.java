package org.petrowich.gallerychecker.services.checks;

import lombok.extern.log4j.Log4j2;
import org.petrowich.gallerychecker.models.checks.enums.CheckAim;
import org.petrowich.gallerychecker.models.master.sites.Site;
import org.petrowich.gallerychecker.models.tasks.CheckTask;
import org.petrowich.gallerychecker.scheduler.ExecutingJobRegister;
import org.petrowich.gallerychecker.scheduler.exceptions.JobException;
import org.petrowich.gallerychecker.scheduler.stored.CheckSchedulerService;
import org.petrowich.gallerychecker.services.master.SiteService;
import org.quartz.JobDataMap;
import org.quartz.SimpleTrigger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.petrowich.gallerychecker.models.checks.enums.CheckAim.toCheckAim;

@Log4j2
@Service
public class CheckTaskService {
    private static final String USERNAME = "username";
    private static final String AIM = "aim";
    private static final String HOST = "host";
    private static final ZoneId zoneId = ZoneId.systemDefault();

    private final CheckSchedulerService checkSchedulerService;
    private final ExecutingJobRegister executingJobRegister;
    private final SiteService siteService;

    public CheckTaskService(CheckSchedulerService checkSchedulerService,
                            ExecutingJobRegister executingJobRegister,
                            SiteService siteService) {
        this.checkSchedulerService = checkSchedulerService;
        this.executingJobRegister = executingJobRegister;
        this.siteService = siteService;
    }

    public List<CheckTask> getAllTasks() {
        log.debug("Finding all check tasks");
        return checkSchedulerService.getAllTriggers().stream()
                .map(this::triggerToTask)
                .collect(Collectors.toList());
    }

    public Optional<CheckTask> getTaskByTriggerId(String triggerId) {
        log.debug("Finding check task by triggerId:{}", triggerId);
        Optional<SimpleTrigger> optionalSimpleTrigger = checkSchedulerService.getTriggerById(triggerId);
        if(optionalSimpleTrigger.isPresent()) {
            CheckTask checkTask = triggerToTask(optionalSimpleTrigger.get());
            return Optional.of(checkTask);
        }
        return Optional.empty();
    }

    public void addTask(CheckTask checkTask) throws JobException {
        log.debug("Adding new check task: {}", checkTask);

        String username = checkTask.getUsername();
        Site site = extractSiteFromTask(checkTask);

        CheckAim checkAim = checkTask.getCheckAim();
        if (checkAim == null) {
            String errorMessage = "Check aim is not specified.";
            log.error(errorMessage);
            throw new JobException(errorMessage, checkTask.getSite());
        }

        if (executingJobRegister.checkJobIsRunning(site)) {
            String errorMessage = String.format("There is another check of %s galleries still running.", site.getHost());
            log.error(errorMessage, site);
            throw new JobException(errorMessage, site);
        }

        Integer aim = checkAim.getAim();
        if (checkTask.isOnce()) {
            checkSchedulerService.scheduleJobAtOnce(site, aim, username);
        } else {
            LocalDateTime startAt = checkTask.getNextFireDateTime();
            Integer periodDays = checkTask.getPeriodDays();
            checkSchedulerService.scheduleJobAtTimeWitPeriod(site, aim, startAt, periodDays, username);
        }
    }

    public void cancelTask(CheckTask checkTask) {
        log.debug("Canceling check task {}", checkTask);
        Optional<SimpleTrigger> optionalSimpleTrigger = checkSchedulerService.getTriggerById(checkTask.getId());
        optionalSimpleTrigger.ifPresent(checkSchedulerService::cancelTaskByTrigger);
    }

    public void interruptTask(String taskId) {
        log.debug("Interrupting check taskId={}", taskId);
        Optional<SimpleTrigger> optionalSimpleTrigger = checkSchedulerService.getTriggerById(taskId);
        optionalSimpleTrigger.ifPresent(checkSchedulerService::interruptTaskByTrigger);
    }

    private Site extractSiteFromTask(CheckTask checkTask) throws JobException {
        log.debug("Extracting site from task");
        Site site = checkTask.getSite();
        if (site != null) {
            Site storedSite = siteService.findById(site.getId());
            if (storedSite != null) {
                checkTask.setSite(storedSite);
                return storedSite;
            } else {
                String errorMessage = "Specified site does not exist.";
                log.error(errorMessage);
                throw new JobException(errorMessage, site);
            }
        } else {
            String errorMessage = "Site is not specified.";
            log.error(errorMessage);
            throw new JobException(errorMessage, new Site());
        }
    }

    private CheckTask triggerToTask(SimpleTrigger trigger) {
        String id = trigger.getKey().getName();

        JobDataMap jobDataMap = trigger.getJobDataMap();
        String username = (String) jobDataMap.get(USERNAME);
        String siteHost = (String) jobDataMap.get(HOST);
        Integer aim = (Integer) jobDataMap.get(AIM);
        CheckAim checkAim = toCheckAim(aim);

        Site site = siteService.findByHost(siteHost);
        if (site == null) {
            site = new Site().setName("not defined");
        }

        LocalDateTime lastFireDateTime = dateToLocalDateTime(trigger.getPreviousFireTime());
        LocalDateTime nextFireDateTime = dateToLocalDateTime(trigger.getNextFireTime());
        long repeatInterval = trigger.getRepeatInterval();
        Integer periodDays = (int) (repeatInterval / 86400000L);
        String status = checkSchedulerService.getTriggerStateName(trigger);

        return new CheckTask()
                .setId(id)
                .setUsername(username)
                .setSite(site)
                .setCheckAim(checkAim)
                .setLastFireDateTime(lastFireDateTime)
                .setNextFireDateTime(nextFireDateTime)
                .setPeriodDays(periodDays)
                .setStatus(status);
    }

    private LocalDateTime dateToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), zoneId);
    }
}
