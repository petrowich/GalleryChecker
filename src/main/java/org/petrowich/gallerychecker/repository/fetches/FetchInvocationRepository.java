package org.petrowich.gallerychecker.repository.fetches;

import org.petrowich.gallerychecker.models.fetches.FetchInvocation;
import org.petrowich.gallerychecker.models.fetches.FetchInvocationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FetchInvocationRepository extends JpaRepository<FetchInvocation, FetchInvocationId> {
    List<FetchInvocation> findInvocationsFetchByFetchId(Integer fetchId);

    @Query("select i from FetchInvocation i where i.fetchId in (:fetchIdList)")
    List<FetchInvocation> findInvocationsFetchByFetchIdList(@Param("fetchIdList") List<Integer> fetchIdList);
}
