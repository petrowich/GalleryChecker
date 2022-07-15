package org.petrowich.gallerychecker.mappers.stored;

import org.modelmapper.ModelMapper;
import org.petrowich.gallerychecker.dto.stored.CheckDto;
import org.petrowich.gallerychecker.mappers.AbstractDtoMapper;
import org.petrowich.gallerychecker.models.checks.Check;
import org.petrowich.gallerychecker.models.master.sites.Site;
import org.petrowich.gallerychecker.models.users.UserInfo;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CheckDtoMapper extends AbstractDtoMapper<Check, CheckDto> {
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

    protected CheckDtoMapper(ModelMapper modelMapper) {
        super(modelMapper, Check.class, CheckDto.class);
    }

    @Override
    protected void setupMapper() {
        getModelMapper().createTypeMap(Check.class, CheckDto.class)
                .addMappings(propertyMap -> propertyMap.skip(CheckDto::setUsername))
                .addMappings(propertyMap -> propertyMap.skip(CheckDto::setSiteHost))
                .addMappings(propertyMap -> propertyMap.skip(CheckDto::setCheckDateTime))
                .setPostConverter(toDtoConverter());
    }

    @Override
    protected CheckDto newDto() {
        return new CheckDto();
    }

    @Override
    public void mapSpecificFields(Check check, CheckDto checkDto) {
        UserInfo userInfo = check.getUserInfo();
        if (userInfo != null) {
            checkDto.setUsername(userInfo.getUsername());
        }

        Site site = check.getSite();
        if (site != null) {
            checkDto.setSiteHost(site.getHost());
        }

        LocalDateTime checkDateTime = check.getCheckDateTime();
        if (checkDateTime != null) {
            checkDto.setCheckDateTime(checkDateTime.format(dateTimeFormatter));
        }
    }
}
