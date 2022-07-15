package org.petrowich.gallerychecker.utils.filereaders;

import lombok.extern.log4j.Log4j2;
import org.petrowich.gallerychecker.dto.external.ExTubeGalleryDto;
import org.petrowich.gallerychecker.utils.filereaders.exceptions.TubeGalleryReaderException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

@Log4j2
public abstract class AbstractTubeGalleryFileReader implements TubeGalleryFileReader {

    @Override
    public List<ExTubeGalleryDto> readCsv(File file) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            return readLines(bufferedReader);
        } catch (Exception exception) {
            log.error("The error in reading downloaded file {}: {}", file.getName(), exception.getMessage(), exception);
        }
        return emptyList();
    }

    private List<ExTubeGalleryDto> readLines(BufferedReader bufferedReader) throws IOException {
        List<ExTubeGalleryDto> exTubeGalleryDtoList = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            try {
                exTubeGalleryDtoList.add(lineToGallery(line));
            } catch (Exception exception) {
                log.error("Reading line error: {}", exception.getMessage(), exception);
            }
        }
        return exTubeGalleryDtoList;
    }

    protected abstract ExTubeGalleryDto lineToGallery(String line) throws TubeGalleryReaderException;
}
