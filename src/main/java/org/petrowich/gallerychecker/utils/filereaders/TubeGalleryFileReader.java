package org.petrowich.gallerychecker.utils.filereaders;

import org.petrowich.gallerychecker.dto.external.ExTubeGalleryDto;

import java.io.File;
import java.util.List;

@FunctionalInterface
public interface TubeGalleryFileReader {
    List<ExTubeGalleryDto> readCsv(File file);
}
