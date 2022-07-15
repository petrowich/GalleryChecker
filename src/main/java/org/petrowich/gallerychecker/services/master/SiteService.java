package org.petrowich.gallerychecker.services.master;

import lombok.extern.log4j.Log4j2;
import org.petrowich.gallerychecker.models.master.sites.Site;
import org.petrowich.gallerychecker.models.master.sites.SiteStoredGalleriesCount;
import org.petrowich.gallerychecker.repository.master.SiteRepository;
import org.petrowich.gallerychecker.services.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class SiteService extends AbstractService<Site, Integer, SiteRepository> {

    @Autowired
    public SiteService(SiteRepository siteRepository) {
        super(siteRepository);
    }

    @Override
    public Collection<Site> findAll() {
        log.debug("Finding all sites");
        return super.findAll();
    }

    public Site findByHost(String host) {
        return getRepository().findByHost(host);
    }

    public Site findByIdWithStatistics(Integer id) {
        log.debug("Finding site with statistics by id={}", id);

        Site site = getRepository().getById(id);
        Optional<SiteStoredGalleriesCount> optionalSiteStoredGalleriesCount = getRepository().findByIdWithStatistics(site);

        return optionalSiteStoredGalleriesCount.map(this::createSiteWithStatistics).orElse(site);
    }

    public Collection<Site> findAllWithStatistics() {
        log.debug("Finding all sites with statistics");

        Collection<Site> sites = super.findAll();
        List<SiteStoredGalleriesCount> siteStoredGalleriesCounts = getRepository().countStoredGalleries();
        sites.forEach(site -> setStoredGalleriesCounts(site, siteStoredGalleriesCounts));

        return sites;
    }

    private Site createSiteWithStatistics(SiteStoredGalleriesCount siteStoredGalleriesCount) {
        Site site = siteStoredGalleriesCount.getSite();
        site.setTubesNumber(siteStoredGalleriesCount.getTubesNumber());
        site.setGalleriesNumber(siteStoredGalleriesCount.getGalleriesNumber());
        site.setUncheckedGalleriesNumber(siteStoredGalleriesCount.getUncheckedGalleriesNumber());
        site.setAvailableCheckedGalleriesNumber(siteStoredGalleriesCount.getAvailableCheckedGalleriesNumber());
        site.setUnavailableCheckedGalleriesNumber(siteStoredGalleriesCount.getUnavailableCheckedGalleriesNumber());
        return site;
    }

    private void setStoredGalleriesCounts(Site site, List<SiteStoredGalleriesCount> siteStoredGalleriesCounts) {
        Optional<SiteStoredGalleriesCount> optionalSiteGalleriesCount = siteStoredGalleriesCounts.stream()
                .filter(galleriesCount -> site.equals(galleriesCount.getSite()))
                .findAny();

        if (optionalSiteGalleriesCount.isPresent()) {
            SiteStoredGalleriesCount siteStoredGalleriesCount = optionalSiteGalleriesCount.get();
            site.setTubesNumber(siteStoredGalleriesCount.getTubesNumber());
            site.setGalleriesNumber(siteStoredGalleriesCount.getGalleriesNumber());
            site.setUncheckedGalleriesNumber(siteStoredGalleriesCount.getUncheckedGalleriesNumber());
            site.setAvailableCheckedGalleriesNumber(siteStoredGalleriesCount.getAvailableCheckedGalleriesNumber());
            site.setUnavailableCheckedGalleriesNumber(siteStoredGalleriesCount.getUnavailableCheckedGalleriesNumber());
        } else {
            site.setTubesNumber(0);
        }
    }
}
