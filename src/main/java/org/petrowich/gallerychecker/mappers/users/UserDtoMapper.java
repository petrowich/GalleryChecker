package org.petrowich.gallerychecker.mappers.users;

import org.modelmapper.ModelMapper;
import org.petrowich.gallerychecker.dto.users.UserInfoDto;
import org.petrowich.gallerychecker.mappers.AbstractDtoMapper;
import org.petrowich.gallerychecker.models.users.UserInfo;
import org.petrowich.gallerychecker.models.users.enums.UserRole;
import org.petrowich.gallerychecker.models.users.enums.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper extends AbstractDtoMapper<UserInfo, UserInfoDto> {

    protected UserDtoMapper(ModelMapper modelMapper) {
        super(modelMapper, UserInfo.class, UserInfoDto.class);
    }

    @Override
    protected void setupMapper() {
        getModelMapper().createTypeMap(UserInfo.class, UserInfoDto.class)
                .setPostConverter(toDtoConverter());
        getModelMapper().createTypeMap(UserInfoDto.class, UserInfo.class)
                .addMappings(propertyMap -> propertyMap.skip(UserInfo::setPassword))
                .setPostConverter(toEntityConverter());
    }

    @Override
    protected UserInfoDto newDto() {
        return new UserInfoDto();
    }

    @Override
    protected void mapSpecificFields(UserInfo source, UserInfoDto destination) {
        UserRole userRole = source.getUserRole();
        if (userRole != null) {
            destination.setRoleName(userRole.getName());
            destination.setRole(userRole.name());
        }

        UserStatus userStatus = source.getUserStatus();
        if (userStatus != null) {
            destination.setStatusName(userStatus.getName());
            destination.setStatus(userStatus.name());
        }
    }

    @Override
    protected void mapSpecificFields(UserInfoDto source, UserInfo destination) {
        String role = source.getRole();
        if (role != null) {
            destination.setUserRole(UserRole.valueOf(role));
        }

        String status = source.getStatus();
        if (status != null) {
            destination.setUserStatus(UserStatus.valueOf(status));
        }
    }
}
