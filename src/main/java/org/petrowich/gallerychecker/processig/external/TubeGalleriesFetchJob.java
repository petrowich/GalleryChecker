package org.petrowich.gallerychecker.processig.external;

import lombok.extern.slf4j.Slf4j;
import org.petrowich.gallerychecker.models.fetches.Fetch;
import org.petrowich.gallerychecker.models.fetches.enums.FetchAim;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.petrowich.gallerychecker.models.users.UserInfo;
import org.petrowich.gallerychecker.scheduler.ExecutingJobRegister;
import org.petrowich.gallerychecker.scheduler.JobRepository;
import org.petrowich.gallerychecker.services.auth.UserService;
import org.petrowich.gallerychecker.services.fetches.FetchService;
import org.petrowich.gallerychecker.services.master.TubeService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.petrowich.gallerychecker.models.fetches.enums.FetchAim.toFetchAim;

@Slf4j
@Component
public abstract class TubeGalleriesFetchJob extends QuartzJobBean implements InterruptableJob {
    private static final String USERNAME = "username";
    private static final String AIM = "aim";
    private static final String STATUS_NEW = "new";

    private final TubeService tubeService;
    private final UserService userService;
    private final FetchService fetchService;
    private final JobRepository jobRepository;
    private final TubeGalleriesFetchProcessor tubeGalleriesFetchProcessor;
    private final ExecutingJobRegister executingJobRegister;

    private Tube tube;

    @Autowired
    protected TubeGalleriesFetchJob(TubeService tubeService,
                                    UserService userService,
                                    FetchService fetchService,
                                    JobRepository jobRepository,
                                    ExecutingJobRegister executingJobRegister,
                                    TubeGalleriesFetchProcessor tubeGalleriesFetchProcessor) {
        this.tubeService = tubeService;
        this.userService = userService;
        this.fetchService = fetchService;
        this.jobRepository = jobRepository;
        this.executingJobRegister = executingJobRegister;
        this.tubeGalleriesFetchProcessor = tubeGalleriesFetchProcessor;
    }

    @PostConstruct
    private void init() {
        Optional<Tube> optionalTube = tubeService.findByHost(getHost());
        optionalTube.ifPresent(value -> tube = value);
        jobRepository.registerJob(tube, this.getClass());
    }
    protected abstract String getHost();

    public Tube getTube() {
        return tube;
    }

    @Override
    protected void executeInternal(@Nullable JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Start new fetch job from {}", tube);

        if (jobExecutionContext == null) {
            log.error("Job execution context is null");
            throw new JobExecutionException("Job execution context is null");
        }

        JobDetail jobDetail = jobExecutionContext.getJobDetail();

        JobKey jobKey = jobDetail.getKey();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();

        Fetch fetch = createNewFetch(jobDataMap);
        fetchService.save(fetch);
        jobDataMap.put("fetchId", fetch.getId());

        executeFetch(jobKey, fetch);

        fetchService.save(fetch);
        log.info("Finish fetch job from {}", tube);
    }

    private Fetch createNewFetch(JobDataMap jobDataMap) {
        String username = (String) jobDataMap.get(USERNAME);
        UserInfo userInfo = userService.findByUsername(username);

        Integer aim = (Integer) jobDataMap.get(AIM);
        FetchAim fetchAim = toFetchAim(aim);

        return new Fetch()
                .setUserInfo(userInfo)
                .setStatus(STATUS_NEW)
                .setFetchDateTime(now())
                .setTube(tube)
                .setFetchAim(fetchAim);
    }

    private void executeFetch(JobKey jobKey, Fetch fetch) {
        if (executingJobRegister.occupyJob(tube, jobKey)) {
            tubeGalleriesFetchProcessor.fetchGalleries(fetch);
            executingJobRegister.releaseJob(tube, jobKey);
        } else {
            String errorMessage = String.format("There is another fetch from %s still running.", tube.getHost());
            log.error(errorMessage);
            fetch.setStatus("cancelled").setErrorMessage(errorMessage);
        }
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        Thread.interrupted();
    }
}
