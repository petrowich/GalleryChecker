package org.petrowich.gallerychecker.processig.external;

import lombok.extern.slf4j.Slf4j;
import org.petrowich.gallerychecker.models.fetches.Fetch;
import org.petrowich.gallerychecker.models.fetches.enums.FetchAim;

@Slf4j
public abstract class TubeGalleriesFetchProcessor {
    public void fetchGalleries(Fetch fetch) {
        FetchAim fetchAim = fetch.getFetchAim();
        try {
            switch (fetchAim) {
                case NEW: addNewGalleries(fetch); break;
                case DELETED: deactivateDeletedGalleries(fetch); break;
                default: {
                    String errorMessage = String.format("There is no action for such aim %s", fetchAim);
                    log.error(errorMessage);
                    fetch.setStatus("Error").setErrorMessage(errorMessage);
                }
            }
        } catch (Exception e) {
            fetch.setStatus("Error").setErrorMessage(e.getMessage());
        }
    }

    protected abstract void addNewGalleries(Fetch fetch);

    protected abstract void deactivateDeletedGalleries(Fetch fetch);
}
