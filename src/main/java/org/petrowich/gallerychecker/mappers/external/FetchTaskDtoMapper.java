package org.petrowich.gallerychecker.mappers.external;

import org.modelmapper.ModelMapper;
import org.petrowich.gallerychecker.dto.external.FetchTaskDto;
import org.petrowich.gallerychecker.mappers.AbstractDtoMapper;
import org.petrowich.gallerychecker.models.tasks.FetchTask;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.petrowich.gallerychecker.models.fetches.enums.FetchAim;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class FetchTaskDtoMapper extends AbstractDtoMapper<FetchTask, FetchTaskDto> {
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

    protected FetchTaskDtoMapper(ModelMapper modelMapper) {
        super(modelMapper, FetchTask.class, FetchTaskDto.class);
    }

    @Override
    protected FetchTaskDto newDto() {
        return new FetchTaskDto();
    }

    @Override
    protected void setupMapper() {
        getModelMapper()
                .createTypeMap(FetchTask.class, FetchTaskDto.class)
                .addMappings(propertyMap -> propertyMap.skip(FetchTaskDto::setTubeId))
                .addMappings(propertyMap -> propertyMap.skip(FetchTaskDto::setHost))
                .addMappings(propertyMap -> propertyMap.skip(FetchTaskDto::setLast))
                .addMappings(propertyMap -> propertyMap.skip(FetchTaskDto::setAim))
                .setPostConverter(toDtoConverter());

        getModelMapper()
                .createTypeMap(FetchTaskDto.class, FetchTask.class)
                .addMappings(propertyMap -> propertyMap.skip(FetchTask::setId))
                .addMappings(propertyMap -> propertyMap.skip(FetchTask::setTube))
                .addMappings(propertyMap -> propertyMap.skip(FetchTask::setFetchAim))
                .addMappings(propertyMap -> propertyMap.skip(FetchTask::setLastFireDateTime))
                .addMappings(propertyMap -> propertyMap.skip(FetchTask::setNextFireDateTime))
                .setPostConverter(toEntityConverter());
    }

    @Override
    public void mapSpecificFields(FetchTask fetchTask, FetchTaskDto fetchTaskDto) {
        Tube tube = fetchTask.getTube();
        if (tube != null) {
            fetchTaskDto.setTubeId(tube.getId());
            fetchTaskDto.setHost(tube.getHost());
        }

        FetchAim fetchAim = fetchTask.getFetchAim();
        if (fetchAim != null) {
            fetchTaskDto.setAim(fetchTask.getFetchAim().toString());
        }

        LocalDateTime lastFireDateTime = fetchTask.getLastFireDateTime();
        if (lastFireDateTime != null) {
            fetchTaskDto.setLast(lastFireDateTime.format(dateTimeFormatter));
        }

        LocalDateTime nextFireDateTime = fetchTask.getNextFireDateTime();
        if (nextFireDateTime != null) {
            fetchTaskDto.setNext(nextFireDateTime.format(dateTimeFormatter));
        }
    }

    @Override
    public void mapSpecificFields(FetchTaskDto fetchTaskDto, FetchTask fetchTask) {
        fetchTask.setTube(new Tube().setId(fetchTaskDto.getTubeId()));

        String fetchAim = fetchTaskDto.getAim();
        if (fetchAim != null) {
            fetchTask.setFetchAim(FetchAim.valueOf(fetchAim));
        }

        String lastFireDateTime = fetchTaskDto.getLast();
        if (lastFireDateTime != null) {
            fetchTask.setLastFireDateTime(LocalDateTime.parse(lastFireDateTime, dateTimeFormatter));
        }

        String nextFireDateTime = fetchTaskDto.getNext();
        if (nextFireDateTime != null) {
            fetchTask.setNextFireDateTime(LocalDateTime.parse(nextFireDateTime, dateTimeFormatter));
        }
    }
}
