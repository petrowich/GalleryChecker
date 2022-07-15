package org.petrowich.gallerychecker.processig.stored;

import lombok.extern.log4j.Log4j2;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.petrowich.gallerychecker.processig.stored.checkers.DefaultStoredGalleryHtmlChecker;
import org.petrowich.gallerychecker.processig.stored.checkers.StoredGalleryHtmlChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Component
public class StoredGalleryHtmlCheckerRegister {
    private static final Map<Tube, StoredGalleryHtmlChecker> checkerMap = new ConcurrentHashMap<>();

    private final DefaultStoredGalleryHtmlChecker defaultStoredGalleryHtmlChecker;

    @Autowired
    public StoredGalleryHtmlCheckerRegister(DefaultStoredGalleryHtmlChecker defaultStoredGalleryHtmlChecker) {
        this.defaultStoredGalleryHtmlChecker = defaultStoredGalleryHtmlChecker;
    }

    public void registerChecker(Tube tube, StoredGalleryHtmlChecker storedGalleryHtmlChecker) {
        log.debug("Registration html gallery checker {} for {}",
                storedGalleryHtmlChecker.getClass().getSimpleName(), tube);
        checkerMap.put(tube, storedGalleryHtmlChecker);
    }

    public StoredGalleryHtmlChecker getChecker(Tube tube) {
        if (checkerMap.containsKey(tube)) {
            StoredGalleryHtmlChecker storedGalleryHtmlChecker = checkerMap.get(tube);
            log.debug("Getting html gallery checker {} for {}",
                    storedGalleryHtmlChecker.getClass().getSimpleName(), tube);
            return checkerMap.get(tube);
        }
        log.debug("Getting default html gallery checker for {}", tube);
        return defaultStoredGalleryHtmlChecker;
    }
}
