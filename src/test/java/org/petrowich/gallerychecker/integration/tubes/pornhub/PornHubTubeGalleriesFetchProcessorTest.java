package org.petrowich.gallerychecker.integration.tubes.pornhub;

import org.junit.jupiter.api.Test;
import org.petrowich.gallerychecker.models.fetches.Fetch;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.petrowich.gallerychecker.models.fetches.enums.FetchAim;
import org.petrowich.gallerychecker.services.fetches.FetchService;
import org.petrowich.gallerychecker.services.master.TubeService;
import org.petrowich.gallerychecker.processig.external.pornhub.PornHubGalleriesFetchProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static java.time.LocalDateTime.now;

@SpringBootTest
class PornHubTubeGalleriesFetchProcessorTest {
    private static final String HOST = "pornhub.com";

    @Autowired
    private TubeService tubeService;

    @Autowired
    private FetchService fetchService;

    @Autowired
    private PornHubGalleriesFetchProcessor pornHubGalleriesFetchProcessor;

    @Test
    void addNewGalleries() {
        Optional<Tube> optionalTube = tubeService.findByHost(HOST);
        Tube tube = optionalTube.get();
        Fetch fetchNew = new Fetch().setTube(tube).setFetchDateTime(now()).setFetchAim(FetchAim.NEW);
        pornHubGalleriesFetchProcessor.fetchGalleries(fetchNew);
        fetchService.save(fetchNew);
    }

    @Test
    void deactivateDeletedGalleries() {
        Optional<Tube> optionalTube = tubeService.findByHost(HOST);
        Tube tube = optionalTube.get();
        Fetch fetchDeleted = new Fetch().setTube(tube).setFetchDateTime(now()).setFetchAim(FetchAim.DELETED);
        pornHubGalleriesFetchProcessor.fetchGalleries(fetchDeleted);
        fetchService.save(fetchDeleted);
    }
}