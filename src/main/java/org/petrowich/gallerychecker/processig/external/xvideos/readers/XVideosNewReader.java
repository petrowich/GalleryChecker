package org.petrowich.gallerychecker.processig.external.xvideos.readers;

import lombok.extern.slf4j.Slf4j;
import org.petrowich.gallerychecker.utils.filereaders.exceptions.TubeGalleryReaderException;
import org.petrowich.gallerychecker.utils.filereaders.AbstractTubeGalleryFileReader;
import org.petrowich.gallerychecker.dto.external.ExTubeGalleryDto;
import org.springframework.stereotype.Component;

import static java.util.Locale.ROOT;

@Slf4j
@Component
public class XVideosNewReader extends AbstractTubeGalleryFileReader {

    private static final String LINE_SEPARATOR = ";";

    @Override
    protected ExTubeGalleryDto lineToGallery(String line) throws TubeGalleryReaderException {
        String[] values = line.split(LINE_SEPARATOR);
        if (values.length < 10) {
            String message = String.format("mismatch in the number of fields (expected >=10, actual %s) in a row:%n%s",
                    values.length, line);
            throw new TubeGalleryReaderException(message);
        }
        return new ExTubeGalleryDto()
                .setUrl(values[0])
                .setDescription(values[1])
                .setDuration(extractDuration(values[2]))
                .setThumbUrl(values[3])
                .setEmbedCode(values[4])
                .setTags(values[5])
                .setModel(values[6])
                .setId(values[7])
                .setNiche(values[8])
                .setQuality(values[9]);
    }

    private String extractDuration(String duration) {
        return duration.toLowerCase(ROOT)
                .replace("sec", "")
                .trim();
    }
}
