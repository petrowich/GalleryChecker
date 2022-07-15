package org.petrowich.gallerychecker.mappers.external;

import org.modelmapper.ModelMapper;
import org.petrowich.gallerychecker.dto.external.FetchDto;
import org.petrowich.gallerychecker.mappers.AbstractDtoMapper;
import org.petrowich.gallerychecker.models.fetches.Fetch;
import org.petrowich.gallerychecker.models.fetches.FetchInvocation;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.petrowich.gallerychecker.models.fetches.enums.FetchAim;
import org.petrowich.gallerychecker.models.users.UserInfo;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class FetchDtoMapper extends AbstractDtoMapper<Fetch, FetchDto> {
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

    protected FetchDtoMapper(ModelMapper modelMapper) {
        super(modelMapper, Fetch.class, FetchDto.class);
    }

    @Override
    protected FetchDto newDto() {
        return new FetchDto();
    }

    @Override
    protected void setupMapper() {
        getModelMapper().createTypeMap(Fetch.class, FetchDto.class)
                .addMappings(propertyMap -> propertyMap.skip(FetchDto::setUsername))
                .addMappings(propertyMap -> propertyMap.skip(FetchDto::setTubeHost))
                .addMappings(propertyMap -> propertyMap.skip(FetchDto::setAim))
                .addMappings(propertyMap -> propertyMap.skip(FetchDto::setFetchDateTime))
                .addMappings(propertyMap -> propertyMap.skip(FetchDto::setInvocationsNumber))
                .setPostConverter(toDtoConverter());
    }

    @Override
    public void mapSpecificFields(Fetch fetch, FetchDto fetchDto) {
        UserInfo userInfo = fetch.getUserInfo();
        if (userInfo != null) {
            fetchDto.setUsername(userInfo.getUsername());
        }

        Tube tube = fetch.getTube();
        if (tube != null) {
            fetchDto.setTubeHost(tube.getHost());
        }

        FetchAim fetchAim = fetch.getFetchAim();
        if (fetchAim != null) {
            fetchDto.setAim(fetchAim.getShortName());
        }

        LocalDateTime fetchDateTime = fetch.getFetchDateTime();
        if (fetchDateTime != null) {
            fetchDto.setFetchDateTime(fetchDateTime.format(dateTimeFormatter));
        }

        List<FetchInvocation> fetchInvocations = fetch.getFetchInvocations();
        if (fetchInvocations != null) {
            fetchDto.setInvocationsNumber(fetchInvocations.size());
        }
    }
}
