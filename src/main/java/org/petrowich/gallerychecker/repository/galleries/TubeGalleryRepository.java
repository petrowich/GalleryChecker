package org.petrowich.gallerychecker.repository.galleries;

import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.petrowich.gallerychecker.models.galleries.TubeGallery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TubeGalleryRepository extends JpaRepository<TubeGallery, Integer> {
    @Query("select g from TubeGallery g where g.tube = :tube and g.externalId in :externalIds")
    List<TubeGallery> findByTubeAndExternalIds(@Param("tube") Tube tube,
                                               @Param("externalIds") List<String> externalIds);

    @Query("select g from TubeGallery g " +
            "where g.tube = :tube and g.active = :active ")
    List<TubeGallery> findByTubeByStatus(@Param("tube") Tube tube,
                                         @Param("active") Boolean active,
                                         Pageable pageable);

    @Query("select g from TubeGallery g " +
            "where g.tube = :tube and g.active = :active and g.videoDate >= :date ")
    List<TubeGallery> findByTubeByStatusFromDate(@Param("tube") Tube tube,
                                                 @Param("active") Boolean active,
                                                 @Param("date") LocalDate date,
                                                 Pageable pageable);
}
