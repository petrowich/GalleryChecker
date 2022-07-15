package org.petrowich.gallerychecker.scheduler.stored;

import lombok.extern.slf4j.Slf4j;
import org.petrowich.gallerychecker.scheduler.JobRepository;
import org.petrowich.gallerychecker.scheduler.SchedulerService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.petrowich.gallerychecker.scheduler.JobType.CHECK;

@Slf4j
@Component
public class CheckSchedulerService extends SchedulerService {
    @Autowired
    public CheckSchedulerService(JobRepository jobRepository, Scheduler scheduler) {
        super(CHECK, jobRepository, scheduler);
    }
}
