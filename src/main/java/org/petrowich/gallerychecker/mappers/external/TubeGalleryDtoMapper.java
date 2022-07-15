package org.petrowich.gallerychecker.mappers.external;

import org.modelmapper.ModelMapper;
import org.petrowich.gallerychecker.dto.external.TubeGalleryDto;
import org.petrowich.gallerychecker.mappers.AbstractDtoMapper;
import org.petrowich.gallerychecker.models.fetches.Fetch;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.petrowich.gallerychecker.models.galleries.TubeGallery;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class TubeGalleryDtoMapper extends AbstractDtoMapper<TubeGallery, TubeGalleryDto> {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    protected TubeGalleryDtoMapper(ModelMapper modelMapper) {
        super(modelMapper, TubeGallery.class, TubeGalleryDto.class);
    }

    @Override
    protected TubeGalleryDto newDto() {
        return new TubeGalleryDto();
    }

    @Override
    public void setupMapper() {
        getModelMapper().createTypeMap(TubeGallery.class, TubeGalleryDto.class)
                .addMappings(propertyMap -> propertyMap.skip(TubeGalleryDto::setTubeId))
                .setPostConverter(toDtoConverter());
    }

    @Override
    public void mapSpecificFields(TubeGallery tubeGallery, TubeGalleryDto tubeGalleryDto) {
        Fetch fetch = tubeGallery.getFetch();
        if (fetch != null) {
            tubeGalleryDto.setFetchId(fetch.getId());
        }
        Tube tube = tubeGallery.getTube();
        if (tube != null) {
            tubeGalleryDto.setTubeId(tube.getId());
        }
        LocalDate date = tubeGallery.getVideoDate();
        if (date != null) {
            tubeGalleryDto.setDate(date.format(dateFormatter));
        }
    }
}
