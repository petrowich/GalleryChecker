package org.petrowich.gallerychecker.mappers.external;

import org.modelmapper.ModelMapper;
import org.petrowich.gallerychecker.dto.external.TubeDto;
import org.petrowich.gallerychecker.mappers.AbstractDtoMapper;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.springframework.stereotype.Component;

@Component
public class TubeDtoMapper extends AbstractDtoMapper<Tube, TubeDto> {

    protected TubeDtoMapper(ModelMapper modelMapper) {
        super(modelMapper, Tube.class, TubeDto.class);
    }

    @Override
    protected TubeDto newDto() {
        return new TubeDto();
    }

    @Override
    protected void setupMapper() {
        getModelMapper().createTypeMap(Tube.class, TubeDto.class)
                .setPostConverter(toDtoConverter());
    }
}
