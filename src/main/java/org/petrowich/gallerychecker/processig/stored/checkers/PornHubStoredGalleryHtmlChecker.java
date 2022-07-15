package org.petrowich.gallerychecker.processig.stored.checkers;

import org.petrowich.gallerychecker.processig.stored.StoredGalleryHtmlCheckerRegister;
import org.petrowich.gallerychecker.services.master.TubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class PornHubStoredGalleryHtmlChecker extends TubeStoredGalleryHtmlChecker {

    private static final String TUBE_HOST = "pornhub.com";
    private static final List<String> signs = new ArrayList<>(List.of("Page Not Found - Pornhub.com",
            "This video is unavailable in your country."));

    @Autowired
    public PornHubStoredGalleryHtmlChecker(TubeService tubeService,
                                           StoredGalleryHtmlCheckerRegister storedGalleryHtmlCheckerRegister) {
        super(tubeService, storedGalleryHtmlCheckerRegister);
    }

    @PostConstruct
    protected void init() {
        super.init();
    }

    @Override
    protected String getHost() {
        return TUBE_HOST;
    }

    @Override
    protected List<String> getDeletionSigns() {
        return signs;
    }
}
