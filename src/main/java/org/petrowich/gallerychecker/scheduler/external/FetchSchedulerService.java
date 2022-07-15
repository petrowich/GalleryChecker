package org.petrowich.gallerychecker.scheduler.external;

import lombok.extern.slf4j.Slf4j;
import org.petrowich.gallerychecker.scheduler.JobRepository;
import org.petrowich.gallerychecker.scheduler.SchedulerService;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.petrowich.gallerychecker.scheduler.JobType.FETCH;

@Slf4j
@Component
public class FetchSchedulerService extends SchedulerService {
    @Autowired
    public FetchSchedulerService(JobRepository jobRepository, Scheduler scheduler) {
        super(FETCH, jobRepository, scheduler);
    }
}
