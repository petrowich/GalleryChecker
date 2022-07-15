package org.petrowich.gallerychecker.processig.stored;

import lombok.extern.slf4j.Slf4j;
import org.petrowich.gallerychecker.models.checks.Check;
import org.petrowich.gallerychecker.models.checks.enums.CheckAim;
import org.petrowich.gallerychecker.models.galleries.StoredGallery;
import org.petrowich.gallerychecker.models.master.sites.Site;
import org.petrowich.gallerychecker.services.checks.CheckService;
import org.petrowich.gallerychecker.services.galleries.StoredGalleryService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class StoredGalleriesCheckProcessor {

    private final StoredGalleryService storedGalleryService;
    private final StoredGalleryCheckExecutor storedGalleryCheckExecutor;
    private final CheckService checkService;

    public StoredGalleriesCheckProcessor(StoredGalleryService storedGalleryService,
                                         StoredGalleryCheckExecutor storedGalleryCheckExecutor,
                                         CheckService checkService) {
        this.storedGalleryService = storedGalleryService;
        this.storedGalleryCheckExecutor = storedGalleryCheckExecutor;
        this.checkService = checkService;
    }

    public void checkGalleries(Check check) {
        log.info("Checking galleries: {}", check);
        checkService.save(check.setStatus("checking"));
        try {
            pickGalleriesForCheck(check).stream()
                    .map(storedGalleryCheckExecutor::executeStoredGalleryAvailabilityCheck)
                    .forEach(storedGalleryService::save);
            check.setStatus("OK");
        } catch (Exception e) {
            log.error("{} {}", check, e.getMessage());
            check.setStatus("Error").setErrorMessage(e.getMessage());
        }
    }

    private List<StoredGallery> pickGalleriesForCheck(Check check) {
        CheckAim checkAim = check.getCheckAim();
        Site site = check.getSite();
        switch (checkAim) {
            case ALL:
                return storedGalleryService.getBySite(site);
            case AVAILABLE:
                return storedGalleryService.getAvailableGalleries(site);
            case UNCHECKED:
                return storedGalleryService.getUncheckedGalleries(site);
            default: {
                String errorMessage = String.format("There is no action for such aim %s", checkAim);
                log.error(errorMessage);
                check.setStatus("Error").setErrorMessage(errorMessage);
                return Collections.emptyList();
            }
        }
    }
}
