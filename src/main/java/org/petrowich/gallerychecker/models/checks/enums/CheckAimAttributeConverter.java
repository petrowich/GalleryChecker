package org.petrowich.gallerychecker.models.checks.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import static org.petrowich.gallerychecker.models.checks.enums.CheckAim.toCheckAim;

@Converter(autoApply = true)
public class CheckAimAttributeConverter implements AttributeConverter<CheckAim, Integer> {
    @Override
    public Integer convertToDatabaseColumn(CheckAim checkAim) {
        return checkAim.getAim();
    }

    @Override
    public CheckAim convertToEntityAttribute(Integer integer) {
        return toCheckAim(integer);
    }
}
