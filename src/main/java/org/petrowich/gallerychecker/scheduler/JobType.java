package org.petrowich.gallerychecker.scheduler;

public enum JobType {
    FETCH ("fetch",
            "fetching tube galleries",
            "hours",
            1) {
        @Override
        public String toString() {
            return getJobName();
        }
    },
    CHECK ("check",
            "checking stored galleries availability",
            "days",
            24) {
        @Override
        public String toString() {
            return getJobName();
        }
    };

    private final String jobName;
    private final String jobDescription;
    private final String jobPeriodUnit;
    private final Integer jobPeriodRate;

    JobType(String jobName, String jobDescription, String jobPeriodUnit, Integer jobPeriodRate) {
        this.jobName = jobName;
        this.jobDescription = jobDescription;
        this.jobPeriodUnit = jobPeriodUnit;
        this.jobPeriodRate = jobPeriodRate;
    }

    public String getJobName() {
        return jobName;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public String getJobPeriodUnit() {
        return jobPeriodUnit;
    }

    public Integer getJobPeriodRate() {
        return jobPeriodRate;
    }
}
