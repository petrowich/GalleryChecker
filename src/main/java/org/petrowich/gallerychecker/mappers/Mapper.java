package org.petrowich.gallerychecker.mappers;


import org.petrowich.gallerychecker.dto.AbstractDto;
import org.petrowich.gallerychecker.models.Model;

public interface Mapper<E extends Model, D extends AbstractDto> {

    E toModel(D dto);

    D toDto(E model);
}
