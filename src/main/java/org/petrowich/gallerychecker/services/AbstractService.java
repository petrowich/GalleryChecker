package org.petrowich.gallerychecker.services;

import lombok.extern.log4j.Log4j2;
import org.petrowich.gallerychecker.models.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

@Log4j2
public class AbstractService<T extends Model, K, S extends JpaRepository<T, K>> {

    private final S repository;

    protected AbstractService(S repository) {
        this.repository = repository;
    }

    protected S getRepository() {
        return repository;
    }

    public T findById(K id) {
        log.debug("{}: find by id={}", repository.getClass().getSimpleName(), id);
       return repository.findById(id).orElse(null);
    }
    public void save(T model) {
        log.debug("{}: save {}", repository.getClass().getSimpleName(), model.getClass().getSimpleName());
        repository.save(model);
    }

    public void saveAll(Collection<T> models) {
        log.debug("{}: save all", repository.getClass().getSimpleName());
        repository.saveAll(models);
    }

    public Collection<T> findAll() {
        log.debug("{}: find all", repository.getClass().getSimpleName());
        return repository.findAll();
    }

    public Page<T> findPage(Integer pageNumber, Integer size, String sortBy, boolean ascending) {
        log.debug("{}: find page pageNumber={} size={} sortBy={}",
                repository.getClass().getSimpleName(), pageNumber, size, sortBy);
        if (sortBy != null && !sortBy.isBlank()) {
            if (ascending) {
                return repository.findAll(PageRequest.of(pageNumber, size, Sort.by(sortBy).ascending()));
            } else {
                return repository.findAll(PageRequest.of(pageNumber, size, Sort.by(sortBy).descending()));
            }
        }
        return repository.findAll(PageRequest.of(pageNumber, size));
    }
}
