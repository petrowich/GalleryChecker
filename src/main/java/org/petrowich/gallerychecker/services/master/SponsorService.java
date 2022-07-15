package org.petrowich.gallerychecker.services.master;

import org.petrowich.gallerychecker.models.master.sponsors.Sponsor;
import org.petrowich.gallerychecker.repository.master.SponsorRepository;
import org.petrowich.gallerychecker.services.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SponsorService extends AbstractService<Sponsor, Integer, SponsorRepository> {
    @Autowired
    public SponsorService(SponsorRepository sponsorRepository) {
        super(sponsorRepository);
    }

    public Sponsor findByName(String name) {
        return getRepository().findByName(name);
    }

}
