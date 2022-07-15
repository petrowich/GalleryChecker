package org.petrowich.gallerychecker.services.fetches;

import lombok.extern.log4j.Log4j2;
import org.petrowich.gallerychecker.models.fetches.Fetch;
import org.petrowich.gallerychecker.models.fetches.FetchInvocation;
import org.petrowich.gallerychecker.repository.fetches.FetchInvocationRepository;
import org.petrowich.gallerychecker.repository.fetches.FetchRepository;
import org.petrowich.gallerychecker.services.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;

@Log4j2
@Service
public class FetchService extends AbstractService<Fetch, Integer, FetchRepository> {

    private final FetchInvocationRepository fetchInvocationRepository;

    @Autowired
    public FetchService(FetchRepository fetchRepository,
                        FetchInvocationRepository fetchInvocationRepository) {
        super(fetchRepository);
        this.fetchInvocationRepository = fetchInvocationRepository;
    }

    @Override
    public Fetch findById(Integer id) {
        log.debug("Finding fetch by id={}", id);
        Fetch fetch = super.findById(id);
        return setInvocationsForFetch(fetch);
    }

    @Override
    public Collection<Fetch> findAll() {
        log.debug("Finding all fetched");
        Collection<Fetch> fetches = super.findAll();
        return fetches.stream()
                .map(this::setInvocationsForFetch)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Fetch> findPage(Integer pageNumber, Integer size, String sortBy, boolean ascending) {
        log.debug("Finding fetches by pageNumber={} size={} sortBy {}", pageNumber, size, sortBy);
        Page<Fetch> fetchPage = super.findPage(pageNumber, size, sortBy, ascending);
        List<Fetch> fetchList = fetchPage.stream().collect(Collectors.toList());
        Map<Fetch, List<FetchInvocation>> fetchListMap = convertFetchListToMap(fetchList);
        fetchPage.forEach(fetch -> fetch.setFetchInvocations(fetchListMap.get(fetch)));

        return fetchPage;
    }

    private Fetch setInvocationsForFetch(Fetch fetch) {
        List<FetchInvocation> invocations = fetchInvocationRepository.findInvocationsFetchByFetchId(fetch.getId());
        return fetch.setFetchInvocations(invocations);
    }

    private Map<Fetch, List<FetchInvocation>> convertFetchListToMap(List<Fetch> fetchList) {
        List<Integer> fetchIdList = fetchList.stream().map(Fetch::getId).collect(Collectors.toList());
        Map<Integer, Fetch> fetchMap = fetchList.stream().collect(Collectors.toMap(Fetch::getId, Function.identity()));
        return fetchInvocationRepository.findInvocationsFetchByFetchIdList(fetchIdList).stream()
                .map(fetchInvocation -> fetchInvocation.setFetch(fetchMap.get(fetchInvocation.getFetchId())))
                .collect(groupingBy(FetchInvocation::getFetch));
    }

    @Override
    public void save(Fetch fetch) {
        log.debug("Saving fetch");
        super.save(fetch);
        List<FetchInvocation> fetchInvocations = setFetchIdForInvocations(fetch);
        fetchInvocationRepository.saveAll(fetchInvocations);
    }

    @Override
    public void saveAll(Collection<Fetch> fetches) {
        log.debug("Saving {} fetches", fetches.size());
        super.saveAll(fetches);
        List<FetchInvocation> fetchInvocations = new ArrayList<>();
        fetches.stream()
                .map(this::setFetchIdForInvocations)
                .forEach(fetchInvocations::addAll);
        fetchInvocationRepository.saveAll(fetchInvocations);
    }

    private List<FetchInvocation> setFetchIdForInvocations(Fetch fetch) {
        List<FetchInvocation> fetchInvocations = fetch.getFetchInvocations();
        if (fetchInvocations != null) {
            fetchInvocations.forEach(fetchInvocation -> fetchInvocation.setFetchId(fetch.getId()));
            return fetchInvocations;
        }
        return emptyList();
    }
}
