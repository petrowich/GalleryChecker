package org.petrowich.gallerychecker.repository.galleries;

import org.petrowich.gallerychecker.models.galleries.StoredGallery;
import org.petrowich.gallerychecker.models.master.sites.Site;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StoredGalleryRepository extends JpaRepository<StoredGallery, Integer> {

    List<StoredGallery> findBySite(Site site);

    @Query("select g from StoredGallery as g " +
            " where g.site = :site " +
            " and g.url in ( :urls) " )
    List<StoredGallery> findBySiteAndUrls(@Param("site") Site site, @Param("urls") List<String> urls);

    @Query("select g from StoredGallery as g " +
            " inner join UploadInfo as u " +
            " on g.uploadInfo = u.id " +
            " where g.site = :site " +
            " and g.checked = false " +
            " order by u.uploadDateTime desc ")
    List<StoredGallery> findUncheckedGalleries(@Param("site") Site site);

    @Query("select g from StoredGallery as g " +
            " inner join UploadInfo as u " +
            " on g.uploadInfo = u.id " +
            " where g.site = :site " +
            " and g.available = true " +
            " order by u.uploadDateTime desc ")
    List<StoredGallery> findAvailableGalleries(@Param("site") Site site);

    @Query("select g from StoredGallery as g " +
            " inner join UploadInfo as u " +
            " on g.uploadInfo = u.id " +
            " where g.site = :site " +
            " and g.checked = true " +
            " and g.available = false " +
            " order by u.uploadDateTime desc ")
    List<StoredGallery> findUnavailableGalleries(@Param("site") Site site);

    @Transactional
    @Modifying
    @Query("update StoredGallery as g " +
            " set g.checked = false," +
            " g.available = true, " +
            " g.statusCode = NULL, " +
            " g.statusMessage = NULL, " +
            " g.error = false, " +
            " g.errorMessage = NULL, " +
            " g.checkDateTime = NULL " +
            " where g.site = :site " )
    void resetCheckedGalleries(@Param("site") Site site);

    @Transactional
    @Modifying
    @Query("update StoredGallery as g " +
            " set g.checked = false," +
            " g.available = true, " +
            " g.statusCode = NULL, " +
            " g.statusMessage = NULL, " +
            " g.error = false, " +
            " g.errorMessage = NULL, " +
            " g.checkDateTime = NULL " +
            " where g.site = :site " +
            " and g.tube = :tube " )
    void resetCheckedGalleries(@Param("site") Site site, @Param("tube") Tube tube);
}
