package org.petrowich.gallerychecker.mappers.stored;

import org.modelmapper.ModelMapper;
import org.petrowich.gallerychecker.dto.stored.StoredGalleryDto;
import org.petrowich.gallerychecker.mappers.AbstractDtoMapper;
import org.petrowich.gallerychecker.models.galleries.StoredGallery;
import org.petrowich.gallerychecker.models.uploads.UploadInfo;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class StoredGalleryDtoMapper extends AbstractDtoMapper<StoredGallery, StoredGalleryDto> {
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

    protected StoredGalleryDtoMapper(ModelMapper modelMapper) {
        super(modelMapper, StoredGallery.class, StoredGalleryDto.class);
    }

    @Override
    protected StoredGalleryDto newDto() {
        return new StoredGalleryDto();
    }

    @Override
    public void setupMapper() {
        getModelMapper().createTypeMap(StoredGallery.class, StoredGalleryDto.class)
                .addMappings(propertyMap -> propertyMap.skip(StoredGalleryDto::setUploadDateTime))
                .setPostConverter(toDtoConverter());

        getModelMapper().createTypeMap(StoredGalleryDto.class, StoredGallery.class)
                .addMappings(propertyMap -> propertyMap.skip(StoredGallery::setGalleryDateTime))
                .addMappings(propertyMap -> propertyMap.skip(StoredGallery::setCheckDateTime))
                .setPostConverter(toEntityConverter());
    }

    @Override
    public void mapSpecificFields(StoredGallery storedGallery, StoredGalleryDto storedGalleryDto) {
        UploadInfo uploadInfo = storedGallery.getUploadInfo();
        if (uploadInfo != null) {
            storedGalleryDto.setUploadDateTime(uploadInfo.getUploadDateTime());
        }
    }

    @Override
    protected void mapSpecificFields(StoredGalleryDto storedGalleryDto, StoredGallery storedGallery) {
        String galleryDatetime = storedGalleryDto.getGalleryDatetime();
        if (galleryDatetime != null && !galleryDatetime.isEmpty()) {
            LocalDateTime galleryDateTime = LocalDateTime.parse(galleryDatetime, dateTimeFormatter);
            storedGallery.setGalleryDateTime(galleryDateTime);
        }
    }
}
