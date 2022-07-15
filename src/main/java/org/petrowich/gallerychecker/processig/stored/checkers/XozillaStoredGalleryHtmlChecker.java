package org.petrowich.gallerychecker.processig.stored.checkers;

import org.petrowich.gallerychecker.processig.stored.StoredGalleryHtmlCheckerRegister;
import org.petrowich.gallerychecker.services.master.TubeService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

public class XozillaStoredGalleryHtmlChecker extends TubeStoredGalleryHtmlChecker {
    private static final String TUBE_HOST = "xozilla.com";
    private static final List<String> signs = new ArrayList<>(List.of("Video is not found"));

    @Autowired
    public XozillaStoredGalleryHtmlChecker(TubeService tubeService,
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
