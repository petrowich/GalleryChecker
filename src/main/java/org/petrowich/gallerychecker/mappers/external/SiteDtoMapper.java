package org.petrowich.gallerychecker.mappers.external;

import org.modelmapper.ModelMapper;
import org.petrowich.gallerychecker.dto.stored.SiteDto;
import org.petrowich.gallerychecker.mappers.AbstractDtoMapper;
import org.petrowich.gallerychecker.models.master.sites.Site;
import org.springframework.stereotype.Component;

@Component
public class SiteDtoMapper extends AbstractDtoMapper<Site, SiteDto> {

    protected SiteDtoMapper(ModelMapper modelMapper) {
        super(modelMapper, Site.class, SiteDto.class);
    }

    @Override
    protected SiteDto newDto() {
        return new SiteDto();
    }

    @Override
    protected void setupMapper() {
        getModelMapper().createTypeMap(Site.class, SiteDto.class)
                .setPostConverter(toDtoConverter());
    }
}
