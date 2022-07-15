package org.petrowich.gallerychecker.services.fetches;

import lombok.extern.slf4j.Slf4j;
import org.petrowich.gallerychecker.models.tasks.FetchTask;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.petrowich.gallerychecker.models.fetches.enums.FetchAim;
import org.petrowich.gallerychecker.scheduler.ExecutingJobRegister;
import org.petrowich.gallerychecker.scheduler.exceptions.JobException;
import org.petrowich.gallerychecker.scheduler.external.FetchSchedulerService;
import org.petrowich.gallerychecker.services.master.TubeService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.petrowich.gallerychecker.models.fetches.enums.FetchAim.toFetchAim;

@Slf4j
@Service
public class FetchTaskService {
    private static final String USERNAME = "username";
    private static final String AIM = "aim";
    private static final String HOST = "host";
    private static final ZoneId zoneId = ZoneId.systemDefault();

    private final FetchSchedulerService fetchSchedulerService;
    private final ExecutingJobRegister executingJobRegister;
    private final TubeService tubeService;

    @Autowired
    public FetchTaskService(FetchSchedulerService fetchSchedulerService,
                            ExecutingJobRegister executingJobRegister,
                            TubeService tubeService) {
        this.fetchSchedulerService = fetchSchedulerService;
        this.executingJobRegister = executingJobRegister;
        this.tubeService = tubeService;
    }

    public List<FetchTask> getAllTasks() {
        log.debug("Finding all fetch tasks");
        return fetchSchedulerService.getAllTriggers().stream()
                .map(this::triggerToTask)
                .collect(Collectors.toList());
    }

    public Optional<FetchTask> getTaskByTriggerId(String triggerId) {
        log.debug("Finding fetch task by triggerId:{}", triggerId);
        Optional<SimpleTrigger> optionalSimpleTrigger = fetchSchedulerService.getTriggerById(triggerId);
        if(optionalSimpleTrigger.isPresent()) {
            FetchTask fetchTask = triggerToTask(optionalSimpleTrigger.get());
            return Optional.of(fetchTask);
        }
        return Optional.empty();
    }

    public void addTask(FetchTask fetchTask) throws JobException {
        log.debug("Adding new fetch task: {}", fetchTask);

        String username = fetchTask.getUsername();
        Tube tube = extractTubeFromTask(fetchTask);

        FetchAim fetchAim = fetchTask.getFetchAim();
        if (fetchAim == null) {
            String errorMessage = "Check aim is not specified.";
            log.error(errorMessage);
            throw new JobException(errorMessage, tube);
        }

        if (executingJobRegister.checkJobIsRunning(tube)) {
            String errorMessage = String.format("There is another fetch from %s still running.", tube.getHost());
            log.error(errorMessage, tube);
            throw new JobException(errorMessage, tube);
        }

        Integer aim = fetchAim.getAim();
        if (fetchTask.isOnce()) {
            fetchSchedulerService.scheduleJobAtOnce(tube, aim, username);
        } else {
            LocalDateTime startAt = fetchTask.getNextFireDateTime();
            Integer periodHours = fetchTask.getPeriodHours();
            fetchSchedulerService.scheduleJobAtTimeWitPeriod(tube, aim, startAt, periodHours, username);
        }
    }

    public void cancelTask(FetchTask fetchTask) {
        log.debug("Canceling fetch task {}", fetchTask);
        String triggerId = fetchTask.getId();
        Optional<SimpleTrigger> optionalSimpleTrigger = fetchSchedulerService.getTriggerById(triggerId);
        optionalSimpleTrigger.ifPresent(fetchSchedulerService::cancelTaskByTrigger);
    }

    public void interruptTask(FetchTask fetchTask) {
        log.debug("Interrupting fetch {}", fetchTask);
        String triggerId = fetchTask.getId();
        Optional<SimpleTrigger> optionalSimpleTrigger = fetchSchedulerService.getTriggerById(triggerId);
        optionalSimpleTrigger.ifPresent(fetchSchedulerService::interruptTaskByTrigger);
    }


    private Tube extractTubeFromTask(FetchTask fetchTask) throws JobException {
        log.debug("Extracting tube from task");
        Tube tube = fetchTask.getTube();
        if (tube != null) {
            Tube storedTube = tubeService.findById(tube.getId());
            if (storedTube != null) {
                fetchTask.setTube(storedTube);
                return storedTube;
            } else {
                String errorMessage = "Specified tube does not exist.";
                log.error(errorMessage);
                throw new JobException(errorMessage, tube);
            }
        } else {
            String errorMessage = "Tube is not specified.";
            log.error(errorMessage);
            throw new JobException(errorMessage, new Tube());
        }
    }

    private FetchTask triggerToTask(SimpleTrigger trigger) {
        if (trigger == null) {
            return null;
        }

        String id = trigger.getKey().getName();
        JobDataMap jobDataMap = trigger.getJobDataMap();
        String username = (String) jobDataMap.get(USERNAME);
        String tubeHost = (String) jobDataMap.get(HOST);
        Integer aim = (Integer) jobDataMap.get(AIM);
        FetchAim fetchAim = toFetchAim(aim);

        LocalDateTime lastFireDateTime = dateToLocalDateTime(trigger.getPreviousFireTime());
        LocalDateTime nextFireDateTime = dateToLocalDateTime(trigger.getNextFireTime());
        long repeatInterval = trigger.getRepeatInterval();
        Integer periodHours = (int) (repeatInterval / 3600000L);
        String status = fetchSchedulerService.getTriggerStateName(trigger);

        Optional<Tube> optionalTube = tubeService.findByHost(tubeHost);
        Tube tube = optionalTube.orElseGet(() -> new Tube().setHost("not defined"));

        return new FetchTask()
                .setId(id)
                .setUsername(username)
                .setTube(tube)
                .setFetchAim(fetchAim)
                .setLastFireDateTime(lastFireDateTime)
                .setNextFireDateTime(nextFireDateTime)
                .setPeriodHours(periodHours)
                .setStatus(status);
    }

    private LocalDateTime dateToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), zoneId);
    }
}
