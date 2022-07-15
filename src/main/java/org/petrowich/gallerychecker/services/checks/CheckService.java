package org.petrowich.gallerychecker.services.checks;

import lombok.extern.log4j.Log4j2;
import org.petrowich.gallerychecker.models.checks.Check;
import org.petrowich.gallerychecker.repository.checks.CheckRepository;
import org.petrowich.gallerychecker.services.AbstractService;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class CheckService extends AbstractService<Check, Integer, CheckRepository> {
    public CheckService(CheckRepository checkRepository) {
        super(checkRepository);
    }
}
