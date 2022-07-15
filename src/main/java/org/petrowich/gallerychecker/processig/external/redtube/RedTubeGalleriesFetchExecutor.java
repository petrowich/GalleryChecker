package org.petrowich.gallerychecker.processig.external.redtube;

import lombok.extern.slf4j.Slf4j;
import org.petrowich.gallerychecker.dto.external.ExTubeGalleryDto;
import org.petrowich.gallerychecker.mappers.external.ExTubeGalleryMapper;
import org.petrowich.gallerychecker.models.fetches.Fetch;
import org.petrowich.gallerychecker.models.fetches.FetchInvocation;
import org.petrowich.gallerychecker.models.galleries.TubeGallery;
import org.petrowich.gallerychecker.processig.external.redtube.reader.RedTubeDeletedReader;
import org.petrowich.gallerychecker.processig.external.redtube.reader.RedTubeNewReader;
import org.petrowich.gallerychecker.utils.FileHelper;
import org.petrowich.gallerychecker.utils.filereaders.TubeGalleryFileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.time.LocalDate.now;
import static java.util.Collections.emptyList;

@Slf4j
@Component
public class RedTubeGalleriesFetchExecutor {
    private static final String TARGET_DIRECTORY = "Tubes/RedTube";
    private static final Integer ONE = 1;
    private static final Integer NUMBER_OF_DAYS_BEFORE_NOW = 7;
    private static final URI allUri = URI.create("http://widgets.redtube.com/_affiliate/saved/download/export_online_mthumb_r.csv");
    private static final URI deletedUri = URI.create("http://widgets.redtube.com/_affiliate/saved/download/export_offline.csv");
    private static final String OK = "OK";
    private static final String ERROR = "Error";

    private final RedTubeNewReader redTubeNewReader;
    private final RedTubeDeletedReader redTubeDeletedReader;
    private final ExTubeGalleryMapper exTubeGalleryMapper;
    private final FileHelper fileHelper;

    @Autowired
    public RedTubeGalleriesFetchExecutor(RedTubeNewReader redTubeNewReader,
                                         RedTubeDeletedReader redTubeDeletedReader,
                                         ExTubeGalleryMapper exTubeGalleryMapper,
                                         FileHelper fileHelper) {
        this.redTubeNewReader = redTubeNewReader;
        this.redTubeDeletedReader = redTubeDeletedReader;
        this.exTubeGalleryMapper = exTubeGalleryMapper;
        this.fileHelper = fileHelper;
    }

    public List<TubeGallery> fetchNewGalleries(Fetch fetch) {
        FetchInvocation fetchInvocation = new FetchInvocation()
                .setFetch(fetch)
                .setInvocationNumber(ONE)
                .setFetchUrl(allUri.toString());

        fetch.setFetchInvocations(new ArrayList<>(List.of(fetchInvocation)));
        try {
            List<ExTubeGalleryDto> exTubeGalleryDtoList = downloadGalleries(redTubeNewReader, allUri);
            fetchInvocation.setFetchedGalleries(exTubeGalleryDtoList.size()).setStatus(OK);

            LocalDate videoDateLimit = now().minusDays(NUMBER_OF_DAYS_BEFORE_NOW);
            return exTubeGalleryDtoList.stream()
                    .map(this::convertExportedToTubeGallery)
                    .filter(Objects::nonNull)
                    .filter(tubeGallery -> tubeGallery.getVideoDate().isAfter(videoDateLimit))
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
                .setFetchUrl(deletedUri.toString());

        fetch.setFetchInvocations(new ArrayList<>(List.of(fetchInvocation)));
        try {
            List<ExTubeGalleryDto> exTubeGalleryDtoList = downloadGalleries(redTubeDeletedReader, deletedUri);

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
        File file = fileHelper.downloadText(subfolder, uri);

        List<ExTubeGalleryDto> exTubeGalleryDtoList = fileReader.readCsv(file)
                .stream()
                .skip(1)
                .collect(Collectors.toList());
        fileHelper.deleteDirectory(subfolder);
        return exTubeGalleryDtoList;
    }

    private TubeGallery convertExportedToTubeGallery(ExTubeGalleryDto exTubeGalleryDto) {
        try {
            return exTubeGalleryMapper.exportedToTubeGallery(exTubeGalleryDto);
        } catch (Exception exception) {
            log.error("{} at row\n{}}", exception.getMessage(), exTubeGalleryDto, exception);
        }
        return null;
    }
}
