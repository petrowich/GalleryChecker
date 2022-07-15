package org.petrowich.gallerychecker.scheduler.exceptions;

import org.petrowich.gallerychecker.models.master.JobOwner;

public class JobException extends Exception {
    private final JobOwner jobOwner;

    public JobException(String message, JobOwner jobOwner) {
        super(message);
        this.jobOwner = jobOwner;
    }

    public JobException(String message, JobOwner jobOwner, Exception exception) {
        super(message, exception);
        this.jobOwner = jobOwner;
    }

    @Override
    public String getMessage() {
        return String.format("Check galleries of %s error: %s", jobOwner, super.getMessage());
    }
}
