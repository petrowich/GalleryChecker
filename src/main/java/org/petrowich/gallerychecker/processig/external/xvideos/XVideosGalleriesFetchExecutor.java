package org.petrowich.gallerychecker.processig.external.xvideos;

import lombok.extern.slf4j.Slf4j;
import org.petrowich.gallerychecker.mappers.external.ExTubeGalleryMapper;
import org.petrowich.gallerychecker.models.fetches.Fetch;
import org.petrowich.gallerychecker.models.fetches.FetchInvocation;
import org.petrowich.gallerychecker.models.galleries.TubeGallery;
import org.petrowich.gallerychecker.processig.external.xvideos.readers.XVideosDeletedReader;
import org.petrowich.gallerychecker.processig.external.xvideos.readers.XVideosNewReader;
import org.petrowich.gallerychecker.utils.FileHelper;
import org.petrowich.gallerychecker.dto.external.ExTubeGalleryDto;
import org.petrowich.gallerychecker.utils.filereaders.TubeGalleryFileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDate.now;
import static java.util.Collections.emptyList;

@Slf4j
@Component
public class XVideosGalleriesFetchExecutor {
    private static final String TARGET_DIRECTORY = "Tubes/XVideos";
    private static final Integer ONE = 1;
    private static final Integer IMPORT_LIMIT = 100;
    private static final Integer NUMBER_OF_DAYS_BEFORE_NOW = 7;
    private static final URI weeklyNewUri = URI.create("https://webmaster-tools.xvideos.com/xvideos.com-export-week.csv.zip");
    private static final URI weeklyDeletedUri = URI.create("https://webmaster-tools.xvideos.com/xvideos.com-deleted-week.csv.zip");
    private static final String OK = "OK";
    private static final String ERROR = "Error";

    private final FileHelper fileHelper;
    private final ExTubeGalleryMapper exTubeGalleryMapper;
    private final XVideosNewReader xVideosNewReader;
    private final XVideosDeletedReader xVideosDeletedReader;

    @Autowired
    public XVideosGalleriesFetchExecutor(FileHelper fileHelper,
                                         ExTubeGalleryMapper exTubeGalleryMapper,
                                         XVideosNewReader xVideosNewReader,
                                         XVideosDeletedReader xVideosDeletedReader) {
        this.fileHelper = fileHelper;
        this.exTubeGalleryMapper = exTubeGalleryMapper;
        this.xVideosNewReader = xVideosNewReader;
        this.xVideosDeletedReader = xVideosDeletedReader;
    }

    public List<TubeGallery> fetchNewGalleries(Fetch fetch) {
        FetchInvocation fetchInvocation = new FetchInvocation()
                .setFetch(fetch)
                .setInvocationNumber(ONE)
                .setFetchUrl(weeklyNewUri.toString());

        fetch.setFetchInvocations(new ArrayList<>(List.of(fetchInvocation)));
        try {
            List<ExTubeGalleryDto> exTubeGalleryDtoList = downloadGalleries(xVideosNewReader, weeklyNewUri);
            fetchInvocation.setFetchedGalleries(exTubeGalleryDtoList.size()).setStatus(OK);
            LocalDate defaultVideoDate = now().minusDays(NUMBER_OF_DAYS_BEFORE_NOW);
            return exTubeGalleryDtoList.stream()
                    .filter(xVideosGallery ->
                            xVideosGallery.getModel().length() != 0 && xVideosGallery.getNiche().length() != 0)
                    .map(exTubeGalleryMapper::exportedToTubeGallery)
                    .map(tubeGallery -> tubeGallery.setVideoDate(defaultVideoDate))
                    .sorted(Comparator.comparing(TubeGallery::getDuration).reversed())
                    .limit(IMPORT_LIMIT)
                    .collect(Collectors.toList());
        } catch (Exception exception) {
            log.error("download new galleries: {}", exception.getMessage(), exception);
            fetchInvocation.setStatus(ERROR).setErrorMessage(exception.getMessage());
        }
        return emptyList();
    }

    public List<TubeGallery> fetchDeletedGalleries(Fetch fetch) {
        FetchInvocation fetchInvocation = new FetchInvocation()
                .setFetch(fetch)
                .setInvocationNumber(ONE)
                .setFetchUrl(weeklyDeletedUri.toString());

        fetch.setFetchInvocations(new ArrayList<>(List.of(fetchInvocation)));
        try {
            List<ExTubeGalleryDto> exTubeGalleryDtoList = downloadGalleries(xVideosDeletedReader, weeklyDeletedUri);

            List<TubeGallery> tubeGalleries = exTubeGalleryDtoList.stream()
                    .map(exTubeGalleryMapper::deletedToTubeGallery)
                    .collect(Collectors.toList());

            fetchInvocation.setFetchedGalleries(tubeGalleries.size()).setStatus(OK);
            return tubeGalleries;
        } catch (Exception exception) {
            log.error("download deleted galleries: {}", exception.getMessage(), exception);
            fetchInvocation.setStatus(ERROR).setErrorMessage(exception.getMessage());
        }
        return emptyList();
    }

    private List<ExTubeGalleryDto> downloadGalleries(TubeGalleryFileReader fileReader, URI uri) throws IOException {
        File subfolder = fileHelper.createTempDirectory(TARGET_DIRECTORY);
        List<File> files = fileHelper.downloadZip(subfolder, uri);
        List<ExTubeGalleryDto> exTubeGalleryDtoList = new ArrayList<>();
        files.stream()
                .map(fileReader::readCsv)
                .forEach(exTubeGalleryDtoList::addAll);
        fileHelper.deleteDirectory(subfolder);
        return exTubeGalleryDtoList;
    }
}
