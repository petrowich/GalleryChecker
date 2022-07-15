package org.petrowich.gallerychecker.models.fetches.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import static org.petrowich.gallerychecker.models.fetches.enums.FetchAim.toFetchAim;

@Converter(autoApply = true)
public class FetchAimAttributeConverter implements AttributeConverter<FetchAim, Integer> {
    @Override
    public Integer convertToDatabaseColumn(FetchAim fetchAim) {
        return fetchAim.getAim();
    }

    @Override
    public FetchAim convertToEntityAttribute(Integer integer) {
        return toFetchAim(integer);
    }
}
