package org.petrowich.gallerychecker.models.master.tubes;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;

public interface TubeStoredGalleriesCount {

    Tube getTube();

    Integer getStoredGalleriesNumber();

    Integer getUncheckedStoredGalleriesNumber();

    Integer getUnavailableStoredGalleriesNumber();
}
