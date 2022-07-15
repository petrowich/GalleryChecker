package org.petrowich.gallerychecker.repository.master;

import org.petrowich.gallerychecker.models.master.sites.Site;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.petrowich.gallerychecker.models.master.tubes.TubeGalleriesCount;
import org.petrowich.gallerychecker.models.master.tubes.TubeStoredGalleriesCount;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

public interface TubeRepository extends JpaRepository<Tube, Integer> {
    @Cacheable("tubes")
    @QueryHints(value = {@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<Tube> findAll();

    @Cacheable("tubes")
    @QueryHints(value = {@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Optional<Tube> findByHost(String host);

    @Cacheable("tubes")
    @QueryHints(value = {@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    @Query("SELECT t as tube" +
            " FROM TubeEmbedVideoHost e " +
            " INNER JOIN e.tube t " +
            " WHERE e.embedVideoHost = :embedVideoHost ")
    Optional<Tube> findByEmbedVideoHost(@Param("embedVideoHost") String embedVideoHost);

    @Query("SELECT tg.tube as tube" +
            ", COUNT(CASE WHEN tg.active = true THEN tg END) as activeTubeGalleriesNumber " +
            ", COUNT(CASE WHEN tg.active = false THEN tg END) as inactiveTubeGalleriesNumber " +
            " FROM TubeGallery tg " +
            " INNER JOIN tg.tube t " +
            " GROUP BY tg.tube, t.id, t.host ")
    List<TubeGalleriesCount> countTubeGalleries();

    @Query("SELECT sg.tube as tube " +
            ", COUNT(sg) as storedGalleriesNumber " +
            ", COUNT(CASE WHEN sg.checked = false THEN sg END) as uncheckedStoredGalleriesNumber " +
            ", COUNT(CASE WHEN sg.available = false THEN sg END) as unavailableStoredGalleriesNumber " +
            " FROM StoredGallery sg " +
            " INNER JOIN sg.tube t " +
            " WHERE sg.site = :site " +
            " GROUP BY sg.tube, t.id, t.host ")
    List<TubeStoredGalleriesCount> countTubesStoredGalleries(@Param("site") Site site);

    @Query("SELECT sg.tube as tube " +
            ", COUNT(sg) as storedGalleriesNumber " +
            ", COUNT(CASE WHEN sg.checked = false THEN sg END) as uncheckedStoredGalleriesNumber " +
            ", COUNT(CASE WHEN sg.available = false THEN sg END) as unavailableStoredGalleriesNumber " +
            " FROM StoredGallery sg " +
            " INNER JOIN sg.tube t " +
            " WHERE sg.site = :site " +
            " AND sg.tube = :tube " +
            " GROUP BY sg.tube, t.id, t.host ")
    Optional<TubeStoredGalleriesCount> countTubeStoredGalleries(@Param("site") Site site, @Param("tube") Tube tube);
}
