package org.petrowich.gallerychecker.integration.tubes.xvideos;

import org.junit.jupiter.api.Test;
import org.petrowich.gallerychecker.models.fetches.Fetch;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.petrowich.gallerychecker.models.fetches.enums.FetchAim;
import org.petrowich.gallerychecker.services.fetches.FetchService;
import org.petrowich.gallerychecker.services.master.TubeService;
import org.petrowich.gallerychecker.processig.external.xvideos.XVideosGalleriesFetchProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static java.time.LocalDateTime.now;

@SpringBootTest
class XVideosTubeGalleriesFetchProcessorTest {
    private static final String HOST = "xvideos.com";

    @Autowired
    private XVideosGalleriesFetchProcessor xVideosGalleriesFetchProcessor;

    @Autowired
    private TubeService tubeService;

    @Autowired
    private FetchService fetchService;

    @Test
    void addNew() {
        Optional<Tube> optionalTube = tubeService.findByHost(HOST);
        Tube tube = optionalTube.get();
        Fetch fetchNew = new Fetch().setTube(tube).setFetchDateTime(now()).setFetchAim(FetchAim.NEW);
        xVideosGalleriesFetchProcessor.fetchGalleries(fetchNew);
        fetchService.save(fetchNew);
    }

    @Test
    void inactivateDeleted() {
        Optional<Tube> optionalTube = tubeService.findByHost(HOST);
        Tube tube = optionalTube.get();
        Fetch fetchDeleted = new Fetch().setTube(tube).setFetchDateTime(now()).setFetchAim(FetchAim.DELETED);
        xVideosGalleriesFetchProcessor.fetchGalleries(fetchDeleted);
        fetchService.save(fetchDeleted);
    }
}