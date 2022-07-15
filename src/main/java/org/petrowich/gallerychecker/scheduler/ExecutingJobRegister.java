package org.petrowich.gallerychecker.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.petrowich.gallerychecker.models.master.JobOwner;
import org.quartz.JobKey;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ExecutingJobRegister {
    private final Map<JobOwner, JobKey> runningFetchMap = new HashMap<>();

    public boolean checkJobIsRunning(JobOwner jobOwner) {
        return runningFetchMap.containsKey(jobOwner);
    }

    public boolean occupyJob(JobOwner jobOwner, JobKey jobKey) {
        log.debug("Occupy job {} of {}", jobKey, jobOwner);
        synchronized (runningFetchMap) {
            if (runningFetchMap.containsKey(jobOwner)) {
                return false;
            }
            runningFetchMap.put(jobOwner, jobKey);
            return true;
        }
    }

    public void releaseJob(JobOwner jobOwner, JobKey jobKey) {
        log.debug("Release job {} of {}", jobKey, jobOwner);
        synchronized (runningFetchMap) {
            runningFetchMap.remove(jobOwner, jobKey);
        }
    }
}
