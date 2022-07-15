package org.petrowich.gallerychecker.repository.checks;

import org.petrowich.gallerychecker.models.checks.Check;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckRepository extends JpaRepository<Check, Integer> {
}
