package org.petrowich.gallerychecker.processig.stored;

import lombok.extern.slf4j.Slf4j;
import org.petrowich.gallerychecker.models.checks.Check;
import org.petrowich.gallerychecker.models.checks.enums.CheckAim;
import org.petrowich.gallerychecker.models.master.sites.Site;
import org.petrowich.gallerychecker.models.users.UserInfo;
import org.petrowich.gallerychecker.scheduler.ExecutingJobRegister;
import org.petrowich.gallerychecker.services.auth.UserService;
import org.petrowich.gallerychecker.services.checks.CheckService;
import org.petrowich.gallerychecker.services.master.SiteService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import static java.time.LocalDateTime.now;

@Slf4j
@Component
@Scope("prototype")
public class StoredGalleriesCheckJob extends QuartzJobBean implements InterruptableJob {
    private static final String USERNAME = "username";
    private static final String AIM = "aim";
    private static final String SITE_HOST = "host";
    private static final String STATUS_NEW = "new";
    private static final String STATUS_INTERRUPTED = "interrupted";

    private final UserService userService;
    private final SiteService siteService;
    private final CheckService checkService;
    private final ExecutingJobRegister executingJobRegister;
    private final StoredGalleriesCheckProcessor storedGalleriesCheckProcessor;
    private JobKey jobKey;
    private Check check;
    private volatile Thread jobThread;

    @Autowired
    public StoredGalleriesCheckJob(UserService userService,
                                   SiteService siteService,
                                   CheckService checkService,
                                   ExecutingJobRegister executingJobRegister,
                                   StoredGalleriesCheckProcessor storedGalleriesCheckProcessor) {
        this.userService = userService;
        this.siteService = siteService;
        this.checkService = checkService;
        this.executingJobRegister = executingJobRegister;
        this.storedGalleriesCheckProcessor = storedGalleriesCheckProcessor;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        log.info("Start new stored galleries checking job");
        jobThread = Thread.currentThread();
        JobDetail jobDetail = jobExecutionContext.getJobDetail();
        jobKey = jobDetail.getKey();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();

        String host = (String) jobDataMap.get(SITE_HOST);
        Site site = siteService.findByHost(host);

        Integer aim = (Integer) jobDataMap.get(AIM);
        CheckAim checkAim = CheckAim.toCheckAim(aim);

        String username = (String) jobDataMap.get(USERNAME);
        UserInfo userInfo = userService.findByUsername(username);

        check = new Check()
                .setUserInfo(userInfo)
                .setStatus(STATUS_NEW)
                .setCheckDateTime(now())
                .setSite(site)
                .setCheckAim(checkAim);

        log.debug("Create new check of {} {} stored galleries", checkAim, site);
        checkService.save(check);
        jobDataMap.put("checkId", check.getId());

        if (executingJobRegister.occupyJob(site, jobKey)) {
            storedGalleriesCheckProcessor.checkGalleries(check);
            executingJobRegister.releaseJob(site, jobKey);
        } else {
            String errorMessage = String.format("There is another check of %s still running.", site.getHost());
            log.error(errorMessage);
            check.setStatus("cancelled").setErrorMessage(errorMessage);
        }

        checkService.save(check);
        log.info("Finish {} job", site.getHost());
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        log.info("Job " + jobKey + "  -- INTERRUPTING --");
        if (jobThread != null) {
            while (jobThread.getState() == Thread.State.RUNNABLE) {
                jobThread.interrupt();
            }
        }
        check.setStatus(STATUS_INTERRUPTED);
        checkService.save(check);
    }
}
