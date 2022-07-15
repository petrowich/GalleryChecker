package org.petrowich.gallerychecker.processig.stored.checkers;

import org.petrowich.gallerychecker.processig.stored.StoredGalleryHtmlCheckerRegister;
import org.petrowich.gallerychecker.services.master.TubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class XHamsterStoredGalleryHtmlChecker extends TubeStoredGalleryHtmlChecker {

    private static final String TUBE_HOST = "xhamster.com";
    private static final List<String> signs = new ArrayList<>(List.of("This video was deleted",
            "Video: access restricted"));

    @Autowired
    public XHamsterStoredGalleryHtmlChecker(TubeService tubeService,
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
