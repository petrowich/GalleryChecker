package org.petrowich.gallerychecker.processig.external.pornhub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.petrowich.gallerychecker.dto.external.ExTubeGalleryDto;
import org.petrowich.gallerychecker.mappers.external.ExTubeGalleryMapper;
import org.petrowich.gallerychecker.models.fetches.Fetch;
import org.petrowich.gallerychecker.models.fetches.FetchInvocation;
import org.petrowich.gallerychecker.models.galleries.TubeGallery;
import org.petrowich.gallerychecker.processig.external.pornhub.dto.PornHubDeletedVideoDto;
import org.petrowich.gallerychecker.processig.external.pornhub.dto.PornHubDeletedVideosDto;
import org.petrowich.gallerychecker.processig.external.pornhub.readers.PornHubReader;
import org.petrowich.gallerychecker.processig.external.pornhub.utils.PornHubUrlBuilder;
import org.petrowich.gallerychecker.utils.FileHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URI;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.time.LocalDate.now;
import static java.util.Collections.emptyList;

@Slf4j
@Component
public class PornHubGalleriesFetchExecutor {
    private static final String TARGET_DIRECTORY = "Tubes/PornHub";
    private static final String WEEKLY_DELETED = "http://www.pornhub.com/webmasters/deleted_videos?page=";
    private static final Integer WEEKLY_DELETED_PAGES = 101;
    private static final Integer NUMBER_OF_DAYS_BEFORE_NOW = 7;
    private static final Integer SLEEP = 1_000;
    private static final String OK = "OK";
    private static final String ERROR = "Error";

    private final FileHelper fileHelper;
    private final ExTubeGalleryMapper exTubeGalleryMapper;
    private final PornHubUrlBuilder pornHubUrlBuilder;
    private final PornHubReader pornHubReader;
    private final PornHubGalleriesFetcher pornHubGalleriesFetcher;
    private final ObjectMapper objectMapper;


    public PornHubGalleriesFetchExecutor(FileHelper fileHelper,
                                         ExTubeGalleryMapper exTubeGalleryMapper,
                                         PornHubUrlBuilder pornHubUrlBuilder,
                                         PornHubReader pornHubReader,
                                         PornHubGalleriesFetcher pornHubGalleriesFetcher, ObjectMapper objectMapper) {
        this.fileHelper = fileHelper;
        this.exTubeGalleryMapper = exTubeGalleryMapper;
        this.pornHubUrlBuilder = pornHubUrlBuilder;
        this.pornHubReader = pornHubReader;
        this.pornHubGalleriesFetcher = pornHubGalleriesFetcher;
        this.objectMapper = objectMapper;
    }

    public List<TubeGallery> fetchNewGalleries(Fetch fetch) {
        List<String> urls = pornHubUrlBuilder.buildUrls();

        if (urls.isEmpty()) {
            log.warn("no urls found for fetch galleries");
            return emptyList();
        }

        List<FetchInvocation> fetchInvocations = IntStream.range(0, urls.size()).boxed()
                .map(urlIndex -> createFetchInvocation(fetch, urlIndex + 1, urls.get(urlIndex)))
                .collect(Collectors.toList());
        fetch.setFetchInvocations(fetchInvocations);

        List<ExTubeGalleryDto> exTubeGalleryDtoList = new ArrayList<>();
        fetchInvocations.forEach(fetchInvocation -> exTubeGalleryDtoList.addAll(downloadGalleries(fetchInvocation)));

        LocalDate defaultVideoDate = now().minusDays(NUMBER_OF_DAYS_BEFORE_NOW);
        return exTubeGalleryDtoList.stream()
                .map(exTubeGalleryMapper::exportedToTubeGallery)
                .map(tubeGallery -> tubeGallery.setVideoDate(defaultVideoDate))
                .filter(distinctByKey(TubeGallery::getExternalId))
                .collect(Collectors.toList());
    }

    public List<TubeGallery> fetchDeletedGalleries(Fetch fetch) {
        List<FetchInvocation> fetchInvocations = IntStream.range(1, WEEKLY_DELETED_PAGES).boxed()
                .map(page -> (createFetchInvocation(fetch, page, String.format("%s%d", WEEKLY_DELETED, page))))
                .collect(Collectors.toList());

        fetch.setFetchInvocations(fetchInvocations);
        fetchInvocations.forEach(this::fetchDeletedGalleriesJson);

        List<TubeGallery> tubeGalleries = fetchInvocations.stream()
                .flatMap(fetchInvocation -> convertDeletedVideosDto(fetchInvocation).stream())
                .collect(Collectors.toList());

        return tubeGalleries.stream()
                .filter(distinctByKey(TubeGallery::getExternalId))
                .collect(Collectors.toList());
    }

    private FetchInvocation createFetchInvocation(Fetch fetch, Integer invocationNumber, String fetchUrl) {
        return new FetchInvocation()
                .setFetch(fetch)
                .setInvocationNumber(invocationNumber)
                .setFetchUrl(fetchUrl);
    }

    private List<ExTubeGalleryDto> downloadGalleries(FetchInvocation fetchInvocation) {
        URI uri = URI.create(fetchInvocation.getFetchUrl());
        try {
            File subfolder = fileHelper.createTempDirectory(TARGET_DIRECTORY);
            File file = fileHelper.downloadText(subfolder, uri);
            List<ExTubeGalleryDto> exTubeGalleryDtoList = pornHubReader.readCsv(file);
            fileHelper.deleteDirectory(subfolder);
            fetchInvocation.setFetchedGalleries(exTubeGalleryDtoList.size()).setStatus(OK);
            return exTubeGalleryDtoList;
        } catch (Exception exception) {
            log.error("download galleries: {}", exception.getMessage(), exception);
            fetchInvocation.setStatus(ERROR).setErrorMessage(exception.getMessage());
        }
        return emptyList();
    }

    private void fetchDeletedGalleriesJson(FetchInvocation fetchInvocation) {
        String url = fetchInvocation.getFetchUrl();
        try {
            Thread.sleep(SLEEP);
            ResponseEntity<String> response = pornHubGalleriesFetcher.fetchStoredGallery(url);
            fetchInvocation.setPayload(response.getBody()).setStatus(response.getStatusCode().toString());
        } catch (InterruptedException exception) {
            log.error("fetching deleted galleries: {}", exception.getMessage(), exception);
            fetchInvocation.setStatus(ERROR).setErrorMessage(exception.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private List<TubeGallery> convertDeletedVideosDto(FetchInvocation fetchInvocation) {
        List<TubeGallery> tubeGalleries = new ArrayList<>();
        String json = fetchInvocation.getPayload();
        if (json == null || json.isEmpty()) {
            String errorMessage = "fetching deleted galleries: no payload";
            log.warn(errorMessage);
            fetchInvocation.setStatus(ERROR).setErrorMessage(errorMessage);
            return tubeGalleries;
        }
        try {
            PornHubDeletedVideosDto deletedVideosDto = objectMapper.readValue(json, PornHubDeletedVideosDto.class);
            List<PornHubDeletedVideoDto> pornHubDeletedVideoDtoList = deletedVideosDto.getVideos();
            if (pornHubDeletedVideoDtoList != null && !pornHubDeletedVideoDtoList.isEmpty()) {
                tubeGalleries = pornHubDeletedVideoDtoList.stream()
                        .map(deletedVideoDto -> new ExTubeGalleryDto().setId(deletedVideoDto.getVideoKey()))
                        .map(exTubeGalleryMapper::deletedToTubeGallery)
                        .collect(Collectors.toList());
                fetchInvocation.setFetchedGalleries(tubeGalleries.size());
            } else {
                log.warn("fetching deleted galleries: empty gallery list in payload");
            }
        } catch (Exception exception) {
            String errorMessage = String.format("converting deleted videos to dto: %s", exception.getMessage());
            log.error(errorMessage, exception);
            fetchInvocation.setStatus(ERROR).setErrorMessage(errorMessage);
        }
        return tubeGalleries;
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
