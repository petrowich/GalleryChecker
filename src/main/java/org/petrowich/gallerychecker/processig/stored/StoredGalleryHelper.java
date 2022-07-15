package org.petrowich.gallerychecker.processig.stored;

import lombok.extern.log4j.Log4j2;
import org.petrowich.gallerychecker.models.galleries.StoredGallery;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.petrowich.gallerychecker.services.master.TubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import static org.petrowich.gallerychecker.utils.EmbedCodeUrlExtractor.extractUrlFromEmbedCode;
import static java.util.Locale.ROOT;

@Log4j2
@Component
public class StoredGalleryHelper {
    private final TubeService tubeService;

    @Autowired
    public StoredGalleryHelper(TubeService tubeService) {
        this.tubeService = tubeService;
    }

    public void extractVideoLinc(StoredGallery storedGallery) {
        log.debug("extracting video url from embed code of {}", storedGallery);
        try {
            String embedUrl = extractUrlFromEmbedCode(storedGallery.getEmbedCode());
            storedGallery.setEmbedUrl(embedUrl).setVideoUrl(embedUrl);
        } catch (Exception e) {
            storedGallery.setError(true).setErrorMessage(e.getMessage());
        }
    }

    public void defineTube(StoredGallery storedGallery) {
        log.debug("defining tube by video url of : {}", storedGallery);
        try {
            defineTubeByVideoUrl(storedGallery);
        } catch (Exception e) {
            storedGallery.setError(true).setErrorMessage(e.getMessage());
        }
    }

    private void defineTubeByVideoUrl(StoredGallery storedGallery) {
        String videoUrl = storedGallery.getEmbedUrl();
        try {
            String embedVideoHost = getHostFromUrl(videoUrl);
            Optional<Tube> optionalTube = tubeService.findByEmbedVideoHost(embedVideoHost);
            if (optionalTube.isEmpty()) {
                log.debug("tube not found, creating new for embed video host: {}", embedVideoHost);
                tubeService.save(new Tube().setHost(embedVideoHost));
                optionalTube = tubeService.findByHost(embedVideoHost);
            }
            if (optionalTube.isPresent()) {
                log.debug("defined tube: {}", optionalTube.get());
                storedGallery.setTube(optionalTube.get());
            }
        } catch (MalformedURLException exception) {
            log.error("wrong video url {} caused an error \n {}", videoUrl, exception.getMessage(), exception);
        }
    }

    private String getHostFromUrl(String videoUrl) throws MalformedURLException {
        URL url = new URL(videoUrl);
        return url.getHost()
                .toLowerCase(ROOT)
                .replace("www.", "");
    }
}
