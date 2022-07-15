package org.petrowich.gallerychecker.integration.tubes.xvideos;

import org.junit.jupiter.api.Test;
import org.petrowich.gallerychecker.models.fetches.Fetch;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.petrowich.gallerychecker.services.fetches.FetchService;
import org.petrowich.gallerychecker.services.master.TubeService;
import org.petrowich.gallerychecker.processig.external.xvideos.XVideosGalleriesFetchExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Optional;

import static java.time.LocalDateTime.now;

@SpringBootTest
class XVideosGalleriesFetchExecutorTest {
    private static final String HOST = "xvideos.com";

    @Autowired
    private TubeService tubeService;

    @Autowired
    private FetchService fetchService;

    @Autowired
    private XVideosGalleriesFetchExecutor xVideosGalleriesFetchExecutor;

    @Test
    void fetchNewGalleries() throws IOException {
        Optional<Tube> optionalTube = tubeService.findByHost(HOST);
        Tube tube = optionalTube.get();
        Fetch fetchNew = new Fetch().setTube(tube).setFetchDateTime(now());
        xVideosGalleriesFetchExecutor.fetchNewGalleries(fetchNew);
        fetchService.save(fetchNew);
    }

    @Test
    void fetchDeletedGalleries() throws IOException {
        Optional<Tube> optionalTube = tubeService.findByHost(HOST);
        Tube tube = optionalTube.get();
        Fetch fetchDeleted = new Fetch().setTube(tube).setFetchDateTime(now());
        xVideosGalleriesFetchExecutor.fetchDeletedGalleries(fetchDeleted);
        fetchService.save(fetchDeleted);
    }
}