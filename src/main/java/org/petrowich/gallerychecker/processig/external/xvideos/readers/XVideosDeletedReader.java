package org.petrowich.gallerychecker.processig.external.xvideos.readers;

import org.petrowich.gallerychecker.utils.filereaders.AbstractTubeGalleryFileReader;
import org.petrowich.gallerychecker.dto.external.ExTubeGalleryDto;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class XVideosDeletedReader extends AbstractTubeGalleryFileReader {
    @Override
    protected ExTubeGalleryDto lineToGallery(String line) {
        return new ExTubeGalleryDto()
                .setId(extractExternalId(line))
                .setDeletedVideoUrl(line);
    }

    private String extractExternalId(String line) {
        Pattern pattern = Pattern.compile("(?<=video)\\d+");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}
