package org.petrowich.gallerychecker.processig.stored.checkers;

import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.petrowich.gallerychecker.processig.stored.StoredGalleryHtmlCheckerRegister;
import org.petrowich.gallerychecker.services.master.TubeService;

import java.util.Optional;

public abstract class TubeStoredGalleryHtmlChecker extends DefaultStoredGalleryHtmlChecker {
    private final TubeService tubeService;
    private final StoredGalleryHtmlCheckerRegister storedGalleryHtmlCheckerRegister;

    protected TubeStoredGalleryHtmlChecker(TubeService tubeService,
                                           StoredGalleryHtmlCheckerRegister storedGalleryHtmlCheckerRegister) {
        this.tubeService = tubeService;
        this.storedGalleryHtmlCheckerRegister = storedGalleryHtmlCheckerRegister;
    }

    protected void init() {
        Optional<Tube> optionalTube = tubeService.findByHost(getHost());
        optionalTube.ifPresent(tube -> storedGalleryHtmlCheckerRegister.registerChecker(tube, this));
    }

    protected abstract String getHost();
}
