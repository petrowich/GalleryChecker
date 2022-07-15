package org.petrowich.gallerychecker.mappers.external;

import org.modelmapper.ModelMapper;
import org.petrowich.gallerychecker.dto.external.FetchInvocationDto;
import org.petrowich.gallerychecker.mappers.AbstractDtoMapper;
import org.petrowich.gallerychecker.models.fetches.FetchInvocation;
import org.springframework.stereotype.Component;

@Component
public class FetchInvocationDtoMapper extends AbstractDtoMapper<FetchInvocation, FetchInvocationDto> {

    protected FetchInvocationDtoMapper(ModelMapper modelMapper) {
        super(modelMapper, FetchInvocation.class, FetchInvocationDto.class);
    }

    @Override
    protected FetchInvocationDto newDto() {
        return new FetchInvocationDto();
    }

    @Override
    protected void setupMapper() {
        getModelMapper()
                .createTypeMap(FetchInvocation.class, FetchInvocationDto.class)
                .setPostConverter(toDtoConverter());
    }
}
