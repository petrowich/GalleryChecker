package org.petrowich.gallerychecker.processig.stored.upload;

import org.petrowich.gallerychecker.dto.stored.StoredGalleryDto;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class StoredGalleryUploadParser {
    private static final String DELIMITER = "\\|";
    private static final String SHARP = "#";

    public List<StoredGalleryDto> readStoredGalleries(InputStream inputStream) throws IOException {
        String text = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        return text.lines()
                .filter(line -> !line.trim().startsWith(SHARP))
                .map(this::lineToStoredGallery)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private StoredGalleryDto lineToStoredGallery(String line) {
        String[] values = line.split(DELIMITER);
        if (values.length >= 4) {
            return new StoredGalleryDto()
                    .setUrl(values[0])
                    .setThumbUrl(values[1])
                    .setEmbedCode(values[2])
                    .setGalleryDatetime(values[3]);
        }
        return null;
    }
}
