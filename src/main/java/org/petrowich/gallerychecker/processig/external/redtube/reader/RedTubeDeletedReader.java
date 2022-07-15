package org.petrowich.gallerychecker.processig.external.redtube.reader;

import org.petrowich.gallerychecker.dto.external.ExTubeGalleryDto;
import org.petrowich.gallerychecker.utils.filereaders.AbstractTubeGalleryFileReader;
import org.springframework.stereotype.Component;

@Component
public class RedTubeDeletedReader extends AbstractTubeGalleryFileReader {

    private static final String LINE_SEPARATOR = "\\|";

    @Override
    protected ExTubeGalleryDto lineToGallery(String line) {
        String[] values = line.split(LINE_SEPARATOR);
        return new ExTubeGalleryDto().setId(values[0]);
    }
}
