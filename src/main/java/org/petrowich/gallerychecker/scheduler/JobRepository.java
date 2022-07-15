package org.petrowich.gallerychecker.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.petrowich.gallerychecker.models.master.JobOwner;
import org.petrowich.gallerychecker.models.master.sites.Site;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.petrowich.gallerychecker.processig.stored.StoredGalleriesCheckJob;
import org.petrowich.gallerychecker.scheduler.exceptions.JobException;
import org.quartz.Job;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class JobRepository {
    private final Map<JobOwner, Class<? extends Job>> jobMap = new ConcurrentHashMap<>();

    public void registerJob(Tube tube, Class<? extends Job> jobClass) {
        log.info("Registration job {} for {}", jobClass.getSimpleName(), tube);
        jobMap.put(tube, jobClass);
    }

    public Class<? extends Job> getJobClass(JobOwner jobOwner) throws JobException {
        log.debug("Getting job for {}", jobOwner);

        if (jobOwner instanceof Site) {
            return StoredGalleriesCheckJob.class;
        }

        Class<? extends Job> jobClass = jobMap.get(jobOwner);

        if (jobClass == null) {
            log.error("Job not found for {}", jobOwner);
            throw new JobException("Job not found", jobOwner);
        }

        return jobClass;
    }

    public boolean isJobExist(JobOwner jobOwner) {
        if (jobOwner instanceof Site) {
            return true;
        }

        return jobMap.containsKey(jobOwner);
    }
}
