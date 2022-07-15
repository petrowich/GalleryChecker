package org.petrowich.gallerychecker.mappers.stored;

import org.modelmapper.ModelMapper;
import org.petrowich.gallerychecker.dto.stored.UploadInfoDto;
import org.petrowich.gallerychecker.mappers.AbstractDtoMapper;
import org.petrowich.gallerychecker.models.master.sites.Site;
import org.petrowich.gallerychecker.models.uploads.UploadInfo;
import org.petrowich.gallerychecker.models.users.UserInfo;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class UploadInfoDtoMapper extends AbstractDtoMapper<UploadInfo, UploadInfoDto> {
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

    protected UploadInfoDtoMapper(ModelMapper modelMapper) {
        super(modelMapper, UploadInfo.class, UploadInfoDto.class);
    }

    @Override
    protected void setupMapper() {
        getModelMapper().createTypeMap(UploadInfo.class, UploadInfoDto.class)
                .addMappings(propertyMap -> propertyMap.skip(UploadInfoDto::setUsername))
                .addMappings(propertyMap -> propertyMap.skip(UploadInfoDto::setSiteHost))
                .addMappings(propertyMap -> propertyMap.skip(UploadInfoDto::setUploadDateTime))
                .addMappings(propertyMap -> propertyMap.skip(UploadInfoDto::setUploadDateTime))
                .setPostConverter(toDtoConverter());
    }

    @Override
    protected UploadInfoDto newDto() {
        return new UploadInfoDto();
    }

    @Override
    public void mapSpecificFields(UploadInfo uploadInfo, UploadInfoDto uploadInfoDto) {
        UserInfo userInfo = uploadInfo.getUserInfo();
        if (userInfo != null) {
            uploadInfoDto.setUsername(userInfo.getUsername());
        }

        Site site = uploadInfo.getSite();
        if (site != null) {
            uploadInfoDto.setSiteHost(site.getHost());
        }

        LocalDateTime fetchDateTime = uploadInfo.getUploadDateTime();
        if (fetchDateTime != null) {
            uploadInfoDto.setUploadDateTime(fetchDateTime.format(dateTimeFormatter));
        }
    }
}
