package org.petrowich.gallerychecker.services.master;

import lombok.extern.log4j.Log4j2;
import org.petrowich.gallerychecker.models.master.sites.Site;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.petrowich.gallerychecker.models.master.tubes.TubeGalleriesCount;
import org.petrowich.gallerychecker.models.master.tubes.TubeStoredGalleriesCount;
import org.petrowich.gallerychecker.repository.master.TubeRepository;
import org.petrowich.gallerychecker.services.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
public class TubeService extends AbstractService<Tube, Integer, TubeRepository> {

    @Autowired
    public TubeService(TubeRepository tubeRepository) {
        super(tubeRepository);
    }

    @Override
    public Collection<Tube> findAll() {
        log.debug("Finding all tubes");
        return super.findAll();
    }

    public Collection<Tube> findAllWithTubeGalleriesStatistics() {
        log.debug("Finding all tubes with statistics");

        Collection<Tube> tubes = super.findAll();
        List<TubeGalleriesCount> tubeGalleriesCounts = new ArrayList<>(getRepository().countTubeGalleries());
        tubes.forEach(tube -> setTubeGalleriesCounts(tube, tubeGalleriesCounts));

        return tubes;
    }

    public Collection<Tube> findAllWithStorageGalleriesStatisticsBySite(Site site) {
        log.debug("Finding all tubes with statistics");

        return getRepository().countTubesStoredGalleries(site).stream()
                .map(this::extractTubeWithStoredGalleriesCounts)
                .collect(Collectors.toList());
    }

    public Optional<Tube> findTubeWithStorageGalleriesStatisticsBySite(Site site, Tube tube) {
        log.debug("Finding all tubes with statistics");

        Optional<TubeStoredGalleriesCount> optionalTubeStoredGalleriesCount = getRepository()
                .countTubeStoredGalleries(site, tube);

        if (optionalTubeStoredGalleriesCount.isPresent()) {
            TubeStoredGalleriesCount tubeStoredGalleriesCount = optionalTubeStoredGalleriesCount.get();
            Tube tubeWithStoredGalleriesCounts = extractTubeWithStoredGalleriesCounts(tubeStoredGalleriesCount);
            return Optional.of(tubeWithStoredGalleriesCounts);
        }
        return Optional.empty();
    }

    @Override
    public Tube findById(Integer id) {
        log.debug("Finding tube by id={}", id);
        Optional<Tube> optionalTube = getRepository().findById(id);
        return optionalTube.orElse(null);
    }

    public Optional<Tube> findByEmbedVideoHost(String embedVideoHost) {
        log.debug("Finding tube by embed video host ={}", embedVideoHost);
        return getRepository().findByEmbedVideoHost(embedVideoHost);
    }

    public Optional<Tube> findByHost(String host) {
        log.debug("Finding tube by host={}", host);
        return getRepository().findByHost(host);
    }

    private void setTubeGalleriesCounts(Tube tube, List<TubeGalleriesCount> tubeGalleriesCounts) {
        Optional<TubeGalleriesCount> optionalTubeGalleriesCount = tubeGalleriesCounts.stream()
                .filter(galleriesCount -> tube.equals(galleriesCount.getTube()))
                .findAny();
        if (optionalTubeGalleriesCount.isPresent()) {
            tube.setActiveTubeGalleriesNumber(optionalTubeGalleriesCount.get().getActiveTubeGalleriesNumber());
            tube.setInactiveTubeGalleriesNumber(optionalTubeGalleriesCount.get().getInactiveTubeGalleriesNumber());
        }
    }

    private Tube extractTubeWithStoredGalleriesCounts(TubeStoredGalleriesCount tubeStoredGalleriesCount) {
        return tubeStoredGalleriesCount.getTube()
                .setStoredGalleriesNumber(tubeStoredGalleriesCount.getStoredGalleriesNumber())
                .setUncheckedStoredGalleriesNumber(tubeStoredGalleriesCount.getUncheckedStoredGalleriesNumber())
                .setUnavailableStoredGalleriesNumber(tubeStoredGalleriesCount.getUnavailableStoredGalleriesNumber());
    }
}
