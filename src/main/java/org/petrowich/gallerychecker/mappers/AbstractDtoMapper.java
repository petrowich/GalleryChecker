package org.petrowich.gallerychecker.mappers;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.petrowich.gallerychecker.dto.AbstractDto;
import org.petrowich.gallerychecker.models.Model;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Objects;

public abstract class AbstractDtoMapper<T extends Model, V extends AbstractDto> implements Mapper<T, V> {
    private final ModelMapper modelMapper;
    private final Class<T> entityClass;
    private final Class<V> dtoClass;

    @Autowired
    protected AbstractDtoMapper(ModelMapper modelMapper, Class<T> entityClass, Class<V> dtoClass) {
        this.modelMapper = modelMapper;
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
    }

    @Override
    public V toDto(T entity) {
        if (Objects.isNull(entity)) {
            return newDto();
        }

        return modelMapper.map(entity, dtoClass);
    }

    @Override
    public T toModel(V dto) {
        if (Objects.isNull(dto)) {
            return null;
        }

        return modelMapper.map(dto, entityClass);
    }

    protected ModelMapper getModelMapper() {
        return modelMapper;
    }

    protected Converter<T, V> toDtoConverter() {
        return context -> {
            T source = context.getSource();
            V destination = context.getDestination();
            mapSpecificFields(source, destination);
            return context.getDestination();
        };
    }

    protected Converter<V, T> toEntityConverter() {
        return context -> {
            V source = context.getSource();
            T destination = context.getDestination();
            mapSpecificFields(source, destination);
            return context.getDestination();
        };
    }

    @PostConstruct
    protected abstract void setupMapper();

    protected abstract V newDto();

    protected void mapSpecificFields(T source, V destination) {
    }

    protected void mapSpecificFields(V source, T destination) {
    }
}
