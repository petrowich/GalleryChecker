package org.petrowich.gallerychecker.repository.fetches;

import org.petrowich.gallerychecker.models.fetches.Fetch;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FetchRepository extends JpaRepository<Fetch, Integer> {
}

