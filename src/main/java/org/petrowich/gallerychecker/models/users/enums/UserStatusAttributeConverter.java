package org.petrowich.gallerychecker.models.users.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class UserStatusAttributeConverter implements AttributeConverter<UserStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(UserStatus userStatus) {
        return userStatus.getStatus();
    }

    @Override
    public UserStatus convertToEntityAttribute(Integer integer) {
        return UserStatus.toStatus(integer);
    }
}
