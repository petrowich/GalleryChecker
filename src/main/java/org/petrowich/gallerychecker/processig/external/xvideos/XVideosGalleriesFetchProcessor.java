package org.petrowich.gallerychecker.processig.external.xvideos;

import lombok.extern.slf4j.Slf4j;
import org.petrowich.gallerychecker.models.fetches.Fetch;
import org.petrowich.gallerychecker.models.galleries.TubeGallery;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.petrowich.gallerychecker.processig.external.TubeGalleriesFetchProcessor;
import org.petrowich.gallerychecker.services.fetches.FetchService;
import org.petrowich.gallerychecker.services.galleries.TubeGalleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;

@Slf4j
@Component
public class XVideosGalleriesFetchProcessor extends TubeGalleriesFetchProcessor {

    private final XVideosGalleriesFetchExecutor xVideosGalleriesFetchExecutor;
    private final FetchService fetchService;
    private final TubeGalleryService tubeGalleryService;

    @Autowired
    public XVideosGalleriesFetchProcessor(XVideosGalleriesFetchExecutor xVideosGalleriesFetchExecutor,
                                          FetchService fetchService,
                                          TubeGalleryService tubeGalleryService) {
        this.xVideosGalleriesFetchExecutor = xVideosGalleriesFetchExecutor;
        this.fetchService = fetchService;
        this.tubeGalleryService = tubeGalleryService;
    }

    @Override
    protected void addNewGalleries(Fetch fetch) {
        log.info("fetching galleries: {}", fetch);

        Tube tube = fetch.getTube();
        fetchService.save(fetch.setStatus("fetching"));
        List<TubeGallery> newTubeGalleries = xVideosGalleriesFetchExecutor.fetchNewGalleries(fetch).stream()
                .map(tubeGallery -> tubeGallery.setTube(tube).setFetch(fetch).setChangeDateTime(now()))
                .collect(Collectors.toList());
        log.debug("fetched {} galleries from {}", newTubeGalleries.size(), tube);

        List<String> externalIds = newTubeGalleries.stream()
                .map(TubeGallery::getExternalId)
                .distinct()
                .collect(Collectors.toList());
        List<TubeGallery> storedTubeGalleries = tubeGalleryService.findAllByTubeAndExternalIds(tube, externalIds);

        log.debug("checking already stored galleries");
        storedTubeGalleries.forEach(storedTubeGallery -> {
                    Optional<TubeGallery> updatedTubeGallery =
                            newTubeGalleries.stream()
                                    .filter(newTubeGallery -> newTubeGallery.getExternalId() != null &&
                                            newTubeGallery.getExternalId().equals(storedTubeGallery.getExternalId()))
                                    .findFirst();
                    updatedTubeGallery.ifPresent(tubeGallery -> {
                        log.trace("found already stored gallery%n{}", tubeGallery);
                        tubeGallery.setId(storedTubeGallery.getId())
                                .setChangeDateTime(storedTubeGallery.getChangeDateTime());
                    });
                }
        );
        Long numberOfStored = newTubeGalleries.stream()
                .filter(storedTubeGallery -> storedTubeGallery.getId() != null)
                .count();
        log.debug("found already stored galleries {} of {} of {}", numberOfStored, newTubeGalleries.size(), tube);

        tubeGalleryService.saveAll(newTubeGalleries);
        fetch.setStatus("OK");
    }

    @Override
    protected void deactivateDeletedGalleries(Fetch fetch) {
        log.info("inactivating galleries: {}", fetch);

        Tube tube = fetch.getTube();
        fetchService.save(fetch.setStatus("fetching"));
        List<TubeGallery> deletedTubeGalleries = xVideosGalleriesFetchExecutor.fetchDeletedGalleries(fetch).stream()
                .map(tubeGallery -> tubeGallery.setTube(tube))
                .collect(Collectors.toList());
        log.debug("fetched {} deleted galleries from {}", deletedTubeGalleries.size(), tube);

        log.debug("finding already stored galleries from {}", tube);

        List<String> externalIds = deletedTubeGalleries.stream()
                .map(TubeGallery::getExternalId)
                .distinct()
                .collect(Collectors.toList());
        List<TubeGallery> storedTubeGalleries = tubeGalleryService.findAllByTubeAndExternalIds(tube, externalIds)
                .stream()
                .map(tubeGallery -> tubeGallery.setFetch(fetch).setActive(false).setChangeDateTime(now()))
                .collect(Collectors.toList());
        log.debug("found {} galleries of {} for deletion", storedTubeGalleries.size(), tube);

        tubeGalleryService.saveAll(storedTubeGalleries);
        fetch.setStatus("OK");
    }
}
