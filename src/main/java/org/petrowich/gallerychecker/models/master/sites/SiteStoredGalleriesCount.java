package org.petrowich.gallerychecker.models.master.sites;

public interface SiteStoredGalleriesCount {
    Site getSite();

    Integer getTubesNumber();

    Integer getGalleriesNumber();

    Integer getUncheckedGalleriesNumber();

    Integer getAvailableCheckedGalleriesNumber();

    Integer getUnavailableCheckedGalleriesNumber();
}
