package org.petrowich.gallerychecker.processig.stored.upload;

import lombok.extern.log4j.Log4j2;
import org.petrowich.gallerychecker.dto.stored.StoredGalleryDto;
import org.petrowich.gallerychecker.mappers.stored.StoredGalleryDtoMapper;
import org.petrowich.gallerychecker.models.master.sites.Site;
import org.petrowich.gallerychecker.models.galleries.StoredGallery;
import org.petrowich.gallerychecker.models.uploads.UploadInfo;
import org.petrowich.gallerychecker.models.users.UserInfo;
import org.petrowich.gallerychecker.services.galleries.StoredGalleryService;
import org.petrowich.gallerychecker.services.uploads.UploadInfoService;
import org.petrowich.gallerychecker.processig.stored.upload.exceptions.GalleryUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.partition;
import static java.time.LocalDateTime.now;

@Log4j2
@Component
public class StoredGalleryUploader {
    private final UploadInfoService uploadInfoService;
    private final StoredGalleryService storedGalleryService;
    private final StoredGalleryUploadParser storedGalleryUploadParser;
    private final StoredGalleryDtoMapper storedGalleryDtoMapper;

    @Autowired
    public StoredGalleryUploader(UploadInfoService uploadInfoService,
                                 StoredGalleryService storedGalleryService,
                                 StoredGalleryUploadParser storedGalleryUploadParser,
                                 StoredGalleryDtoMapper storedGalleryDtoMapper) {
        this.uploadInfoService = uploadInfoService;
        this.storedGalleryService = storedGalleryService;
        this.storedGalleryUploadParser = storedGalleryUploadParser;
        this.storedGalleryDtoMapper = storedGalleryDtoMapper;
    }

    public void uploadFromFile(MultipartFile file, Site site, UserInfo user) {
        if (file.isEmpty()) {
            throw new GalleryUploadException("Failed to store empty file");
        }

        UploadInfo uploadInfo = new UploadInfo()
                .setUploadDateTime(now())
                .setSite(site)
                .setUserInfo(user)
                .setStatus("uploading");

        uploadInfoService.save(uploadInfo);
        upload(uploadInfo, file, site);
        uploadInfoService.save(uploadInfo);
    }

    private void upload(UploadInfo uploadInfo, MultipartFile file, Site site) {
        boolean isError = false;
        Path path = Paths.get(Objects.requireNonNull(file.getOriginalFilename()));

        try (InputStream inputStream = file.getInputStream()) {
            uploadInfo.setFileName(file.getOriginalFilename());

            log.debug("Reading from uploaded file {}", path);
            List<StoredGalleryDto> storedGalleryUploadDtoList = storedGalleryUploadParser.readStoredGalleries(inputStream);
            log.debug("Read {} valid records", storedGalleryUploadDtoList.size());

            List<StoredGallery> storedGalleries = convertToStoredGalleries(storedGalleryUploadDtoList,
                    site,
                    uploadInfo);

            evaluateUploadStatistics(storedGalleries, uploadInfo);

            log.debug("Saving {} uploaded galleries", storedGalleries.size());
            storedGalleryService.saveAll(storedGalleries);
        } catch (Exception exception) {
            isError = true;
            uploadInfo.setStatus("Error").setErrorMessage(exception.getMessage());
            log.error(exception);
            exception.printStackTrace();
        }

        deleteFile(path);

        if (!isError) {
            uploadInfo.setStatus("OK");
        }
    }

    private List<StoredGallery> convertToStoredGalleries(List<StoredGalleryDto> storedGalleryUploadDtoList,
                                                Site site,
                                                UploadInfo uploadInfo) {
        log.debug("Finding existing records for {}", site);
        Map<String, StoredGallery> persistedGalleries = getPersistedGalleries(storedGalleryUploadDtoList, site);
        log.debug("Found {} records", persistedGalleries.size());

        log.debug("Converting file records to galleries");
        return storedGalleryUploadDtoList.stream()
                .map(storedGalleryDtoMapper::toModel)
                .map(storedGallery -> storedGallery.setUploadInfo(uploadInfo).setSite(site))
                .map(storedGallery -> persistedGalleries.getOrDefault(storedGallery.getUrl(), storedGallery))
                //.filter(storedGallery -> !persistedGalleries.containsKey(storedGallery.getUrl()))
                .collect(Collectors.toList());
    }

    private TreeMap<String, StoredGallery> getPersistedGalleries(List<StoredGalleryDto> storedGalleryUploadDtoList, Site site) {
        List<String> storedGalleryUrlList = storedGalleryUploadDtoList.stream()
                .map(StoredGalleryDto::getUrl)
                .collect(Collectors.toList());

        List<List<String>> partsOfUrls = partition(storedGalleryUrlList, 1000);

        Map<String, StoredGallery> stringStoredGalleryMap = partsOfUrls.stream()
                .flatMap(urls -> storedGalleryService.getBySiteAndUrls(site, urls).stream())
                .collect(Collectors.toMap(
                StoredGallery::getUrl,
                Function.identity()
        ));

        return new TreeMap<>(stringStoredGalleryMap);
    }

    private void evaluateUploadStatistics(List<StoredGallery> storedGalleryList, UploadInfo uploadInfo) {
        long newUploadedGalleriesNumber = storedGalleryList.stream()
                .filter(storedGallery -> storedGallery.getId() == null)
                .count();
        uploadInfo.setUploadedGalleriesNumber(storedGalleryList.size())
                .setNewUploadedGalleriesNumber((int) newUploadedGalleriesNumber);
    }

    private void deleteFile(Path path) {
        try {
            log.debug("Deleting uploaded file {}", path);
            Files.delete(path);
        } catch (IOException exception) {
            log.error(exception);
            exception.printStackTrace();
        }
    }
}
