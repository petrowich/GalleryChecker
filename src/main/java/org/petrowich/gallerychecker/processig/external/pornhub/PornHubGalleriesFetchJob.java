package org.petrowich.gallerychecker.processig.external.pornhub;

import org.petrowich.gallerychecker.processig.external.TubeGalleriesFetchJob;
import org.petrowich.gallerychecker.scheduler.ExecutingJobRegister;
import org.petrowich.gallerychecker.scheduler.JobRepository;
import org.petrowich.gallerychecker.services.auth.UserService;
import org.petrowich.gallerychecker.services.fetches.FetchService;
import org.petrowich.gallerychecker.services.master.TubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PornHubGalleriesFetchJob extends TubeGalleriesFetchJob {
    private static final String HOST = "pornhub.com";

    @Autowired
    public PornHubGalleriesFetchJob(TubeService tubeService,
                                    UserService userService,
                                    FetchService fetchService,
                                    JobRepository jobRepository,
                                    ExecutingJobRegister executingJobRegister,
                                    PornHubGalleriesFetchProcessor pornHubGalleriesFetchProcessor) {
        super(tubeService, userService, fetchService, jobRepository, executingJobRegister, pornHubGalleriesFetchProcessor);
    }

    @Override
    protected String getHost() {
        return HOST;
    }
}
