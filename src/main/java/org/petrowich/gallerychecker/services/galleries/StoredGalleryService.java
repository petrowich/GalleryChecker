package org.petrowich.gallerychecker.services.galleries;

import org.petrowich.gallerychecker.models.galleries.StoredGallery;
import org.petrowich.gallerychecker.models.master.sites.Site;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.petrowich.gallerychecker.repository.galleries.StoredGalleryRepository;
import org.petrowich.gallerychecker.services.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoredGalleryService extends AbstractService<StoredGallery, Integer, StoredGalleryRepository> {
    @Autowired
    public StoredGalleryService(StoredGalleryRepository storedGalleryRepository) {
        super(storedGalleryRepository);
    }

    public List<StoredGallery> getBySite(Site site) {
        return getRepository().findBySite(site);
    }

    public List<StoredGallery> getBySiteAndUrls(Site site, List<String> urls) {
        return getRepository().findBySiteAndUrls(site, urls);
    }

    public List<StoredGallery> getUncheckedGalleries(Site site) {
        return getRepository().findUncheckedGalleries(site);
    }

    public List<StoredGallery> getAvailableGalleries(Site site) {
        return getRepository().findAvailableGalleries(site);
    }

    public List<StoredGallery> getUnavailableGalleries(Site site) {
        return getRepository().findUnavailableGalleries(site);
    }

    public void resetCheckedGalleries(Site site) {
        getRepository().resetCheckedGalleries(site);
    }

    public void resetCheckedGalleries(Site site, Tube tube) {
        getRepository().resetCheckedGalleries(site, tube);
    }
}
