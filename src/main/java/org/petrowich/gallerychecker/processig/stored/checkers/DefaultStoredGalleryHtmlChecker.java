package org.petrowich.gallerychecker.processig.stored.checkers;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.*;

@Log4j2
@Component
public class DefaultStoredGalleryHtmlChecker implements StoredGalleryHtmlChecker {
    private static final List<String> signs = new ArrayList<>(List.of("This video was deleted",
            "You are not allowed to watch this video",
            "Embedding video is disabled for this site for security reasons"));

    @Override
    public boolean isStoredGalleryAvailable(String html) {
        log.debug("Analysing gallery html");
        boolean available = getDeletionSigns().stream()
                .map(sign -> sign.toLowerCase(Locale.ROOT))
                .noneMatch(sign -> html.toLowerCase(Locale.ROOT).contains(sign.toLowerCase(Locale.ROOT)));
        log.info("Gallery availability: {}", available);
        return available;
    }
    protected List<String> getDeletionSigns() {
        return signs;
    }
}
