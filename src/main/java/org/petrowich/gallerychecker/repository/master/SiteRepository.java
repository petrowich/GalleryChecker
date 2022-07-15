package org.petrowich.gallerychecker.repository.master;

import org.petrowich.gallerychecker.models.master.sites.Site;
import org.petrowich.gallerychecker.models.master.sites.SiteStoredGalleriesCount;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface SiteRepository extends JpaRepository<Site, Integer> {

    @Cacheable("sites")
    @QueryHints(value = {@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<Site> findAll();

    @Cacheable("sites")
    @QueryHints(value = {@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Site findByHost(String host);

    @Query("SELECT sg.site as site " +
            ", COUNT(distinct sg.tube) as tubesNumber " +
            ", COUNT(sg) as galleriesNumber " +
            ", COUNT(CASE WHEN sg.checked = false THEN sg END) as uncheckedGalleriesNumber " +
            ", COUNT(CASE WHEN sg.checked = true and sg.available = true THEN sg END) as availableCheckedGalleriesNumber " +
            ", COUNT(CASE WHEN sg.checked = true and sg.available = false THEN sg END) as unavailableCheckedGalleriesNumber " +
            " FROM StoredGallery sg " +
            " INNER JOIN sg.site s " +
            " LEFT JOIN sg.tube t " +
            " WHERE sg.site = :site " +
            " GROUP BY sg.site, s.id, s.name, s.host, s.active ")
    Optional<SiteStoredGalleriesCount> findByIdWithStatistics(Site site);

    @Query("SELECT sg.site as site " +
            ", COUNT(distinct sg.tube) as tubesNumber " +
            ", COUNT(sg) as galleriesNumber " +
            ", COUNT(CASE WHEN sg.checked = false THEN sg END) as uncheckedGalleriesNumber " +
            ", COUNT(CASE WHEN sg.checked = true and sg.available = true THEN sg END) as availableCheckedGalleriesNumber " +
            ", COUNT(CASE WHEN sg.checked = true and sg.available = false THEN sg END) as unavailableCheckedGalleriesNumber " +
            " FROM StoredGallery sg " +
            " INNER JOIN sg.site s " +
            " LEFT JOIN sg.tube t " +
            " GROUP BY sg.site, s.id, s.name, s.host, s.active ")
    List<SiteStoredGalleriesCount> countStoredGalleries();
}
