package org.petrowich.gallerychecker.repository.master;

import org.petrowich.gallerychecker.models.master.sponsors.Sponsor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SponsorRepository extends JpaRepository<Sponsor, Integer> {
    Sponsor findByName(String name);
}
