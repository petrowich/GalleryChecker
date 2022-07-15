package org.petrowich.gallerychecker.processig.stored;

import lombok.extern.slf4j.Slf4j;
import org.petrowich.gallerychecker.models.galleries.StoredGallery;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.petrowich.gallerychecker.processig.stored.checkers.StoredGalleryHtmlChecker;
import org.petrowich.gallerychecker.processig.stored.fetch.StoredGalleryFetcher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

@Slf4j
@Component
public class StoredGalleryCheckExecutor {
    private static final int AWAITING = 5_000;

    private final StoredGalleryHelper storedGalleryHelper;
    private final StoredGalleryFetcher storedGalleryFetcher;
    private final StoredGalleryHtmlCheckerRegister storedGalleryHtmlCheckerRegister;

    public StoredGalleryCheckExecutor(StoredGalleryHelper storedGalleryHelper,
                                      StoredGalleryFetcher storedGalleryFetcher,
                                      StoredGalleryHtmlCheckerRegister storedGalleryHtmlCheckerRegister) {
        this.storedGalleryHelper = storedGalleryHelper;
        this.storedGalleryFetcher = storedGalleryFetcher;
        this.storedGalleryHtmlCheckerRegister = storedGalleryHtmlCheckerRegister;
    }

    public StoredGallery executeStoredGalleryAvailabilityCheck(StoredGallery storedGallery) {
        try {
            storedGalleryHelper.extractVideoLinc(storedGallery);
            checkStoredGallery(storedGallery);
            storedGallery.setChecked(true);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            storedGallery.setAvailable(false)
                    .setChecked(true)
                    .setError(true)
                    .setErrorMessage(exception.getMessage());
        }

        try {
            sleep(AWAITING);
        } catch (InterruptedException exception) {
            currentThread().interrupt();
        }

        return storedGallery;
    }

    private void checkStoredGallery(StoredGallery storedGallery) {
        ResponseEntity<String> responseEntity = storedGalleryFetcher.fetchStoredGallery(storedGallery);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String html = responseEntity.getBody();
            storedGalleryHelper.defineTube(storedGallery);
            Tube tube = storedGallery.getTube();
            StoredGalleryHtmlChecker storedGalleryHtmlChecker = storedGalleryHtmlCheckerRegister.getChecker(tube);
            boolean isStoredGalleryAvailable = storedGalleryHtmlChecker.isStoredGalleryAvailable(html);
            storedGallery.setAvailable(isStoredGalleryAvailable);
        } else {
            storedGallery.setAvailable(false);
        }
    }
}
