package org.petrowich.gallerychecker.processig.external.xvideos;

import org.petrowich.gallerychecker.processig.external.TubeGalleriesFetchJob;
import org.petrowich.gallerychecker.scheduler.ExecutingJobRegister;
import org.petrowich.gallerychecker.scheduler.JobRepository;
import org.petrowich.gallerychecker.services.auth.UserService;
import org.petrowich.gallerychecker.services.fetches.FetchService;
import org.petrowich.gallerychecker.services.master.TubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class XVideosGalleriesFetchJob extends TubeGalleriesFetchJob {
    private static final String HOST = "xvideos.com";

    @Autowired
    public XVideosGalleriesFetchJob(TubeService tubeService,
                                    UserService userService,
                                    FetchService fetchService,
                                    JobRepository jobRepository,
                                    ExecutingJobRegister executingJobRegister,
                                    XVideosGalleriesFetchProcessor xVideosGalleriesFetchProcessor) {
        super(tubeService,
                userService,
                fetchService,
                jobRepository,
                executingJobRegister,
                xVideosGalleriesFetchProcessor);
    }

    @Override
    protected String getHost() {
        return HOST;
    }
}
