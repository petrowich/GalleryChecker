package org.petrowich.gallerychecker.services.galleries;

import lombok.extern.slf4j.Slf4j;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.petrowich.gallerychecker.models.galleries.TubeGallery;
import org.petrowich.gallerychecker.repository.galleries.TubeGalleryRepository;
import org.petrowich.gallerychecker.services.AbstractService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import static com.google.common.collect.Lists.partition;

@Slf4j
@Service
public class TubeGalleryService extends AbstractService<TubeGallery, Integer, TubeGalleryRepository> {
    protected TubeGalleryService(TubeGalleryRepository tubeGalleryRepository) {
        super(tubeGalleryRepository);
    }

    public void saveAll(List<TubeGallery> tubeGalleries) {
        getRepository().saveAll(tubeGalleries);
    }

    public List<TubeGallery> findAllByTubeAndExternalIds(Tube tube, List<String> externalIds) {
        log.debug("Finding all by tubes and externalIds of {} and {} externalIds", tube, externalIds.size());

        List<List<String>> partsOfExternalIds = partition(externalIds, 1000);
        List<TubeGallery> result = new ArrayList<>();

        partsOfExternalIds.forEach(partOfExternalIds ->
                result.addAll(getRepository().findByTubeAndExternalIds(tube, partOfExternalIds)));

        return result;
    }

    public List<TubeGallery> findByTubeByStatus(Tube tube, Boolean active, Integer limit) {
        log.debug("Finding galleries by tube {} and active={}", tube, active);
        Pageable pageable = PageRequest.of(0, limit, Sort.by("videoDate").descending());
        return getRepository().findByTubeByStatus(tube, active, pageable);
    }

    public List<TubeGallery> findByTubeByStatusFromDate(Tube tube, Boolean active, LocalDate date, Integer limit) {
        log.debug("Finding galleries by tube {} and active={} added after {}", tube, active, date);
        Pageable pageable = PageRequest.of(0, limit, Sort.by("videoDate").descending());
        return getRepository().findByTubeByStatusFromDate(tube, active, date, pageable);
    }
}
