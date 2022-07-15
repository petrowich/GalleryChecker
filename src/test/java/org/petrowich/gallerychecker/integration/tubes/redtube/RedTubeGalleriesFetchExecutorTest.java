package org.petrowich.gallerychecker.integration.tubes.redtube;

import org.junit.jupiter.api.Test;
import org.petrowich.gallerychecker.models.fetches.Fetch;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.petrowich.gallerychecker.services.fetches.FetchService;
import org.petrowich.gallerychecker.services.master.TubeService;
import org.petrowich.gallerychecker.processig.external.redtube.RedTubeGalleriesFetchExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static java.time.LocalDateTime.now;

@SpringBootTest
class RedTubeGalleriesFetchExecutorTest {

    private static final String HOST = "redtube.com";

    @Autowired
    private TubeService tubeService;

    @Autowired
    private FetchService fetchService;

    @Autowired
    private RedTubeGalleriesFetchExecutor redTubeGalleriesFetchExecutor;

    @Test
    void fetchNewGalleries() {
        Optional<Tube> optionalTube = tubeService.findByHost(HOST);
        Tube tube = optionalTube.get();
        Fetch fetchNew = new Fetch().setTube(tube).setFetchDateTime(now());
        redTubeGalleriesFetchExecutor.fetchNewGalleries(fetchNew);
        fetchService.save(fetchNew);
    }

    @Test
    void fetchDeletedGalleries() {
        Optional<Tube> optionalTube = tubeService.findByHost(HOST);
        Tube tube = optionalTube.get();
        Fetch fetchDeleted = new Fetch().setTube(tube).setFetchDateTime(now());
        redTubeGalleriesFetchExecutor.fetchDeletedGalleries(fetchDeleted);
        fetchService.save(fetchDeleted);
    }
}