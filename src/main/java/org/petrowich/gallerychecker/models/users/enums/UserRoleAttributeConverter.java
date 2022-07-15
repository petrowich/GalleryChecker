package org.petrowich.gallerychecker.models.users.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class UserRoleAttributeConverter implements AttributeConverter<UserRole, Integer> {
    @Override
    public Integer convertToDatabaseColumn(UserRole userRole) {
        return userRole.getRole();
    }

    @Override
    public UserRole convertToEntityAttribute(Integer integer) {
        return UserRole.toRole(integer);
    }
}
