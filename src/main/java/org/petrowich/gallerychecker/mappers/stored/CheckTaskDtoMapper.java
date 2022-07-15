package org.petrowich.gallerychecker.mappers.stored;

import org.modelmapper.ModelMapper;
import org.petrowich.gallerychecker.dto.stored.CheckTaskDto;
import org.petrowich.gallerychecker.mappers.AbstractDtoMapper;
import org.petrowich.gallerychecker.models.checks.enums.CheckAim;
import org.petrowich.gallerychecker.models.master.sites.Site;
import org.petrowich.gallerychecker.models.tasks.CheckTask;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CheckTaskDtoMapper extends AbstractDtoMapper<CheckTask, CheckTaskDto> {
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

    public CheckTaskDtoMapper(ModelMapper modelMapper) {
        super(modelMapper, CheckTask.class, CheckTaskDto.class);
    }

    @Override
    protected void setupMapper() {
        getModelMapper()
                .createTypeMap(CheckTask.class, CheckTaskDto.class)
                .addMappings(propertyMap -> propertyMap.skip(CheckTaskDto::setHost))
                .addMappings(propertyMap -> propertyMap.skip(CheckTaskDto::setLast))
                .addMappings(propertyMap -> propertyMap.skip(CheckTaskDto::setNext))
                .addMappings(propertyMap -> propertyMap.skip(CheckTaskDto::setAim))
                .setPostConverter(toDtoConverter());

        getModelMapper()
                .createTypeMap(CheckTaskDto.class, CheckTask.class)
                .addMappings(propertyMap -> propertyMap.skip(CheckTask::setId))
                .addMappings(propertyMap -> propertyMap.skip(CheckTask::setSite))
                .addMappings(propertyMap -> propertyMap.skip(CheckTask::setCheckAim))
                .addMappings(propertyMap -> propertyMap.skip(CheckTask::setLastFireDateTime))
                .addMappings(propertyMap -> propertyMap.skip(CheckTask::setNextFireDateTime))
                .setPostConverter(toEntityConverter());
    }

    @Override
    protected CheckTaskDto newDto() {
        return new CheckTaskDto();
    }

    @Override
    public void mapSpecificFields(CheckTask checkTask, CheckTaskDto checkTaskDto) {
        Site site = checkTask.getSite();
        if (site != null) {
            checkTaskDto.setHost(site.getHost());
        }

        CheckAim checkAim = checkTask.getCheckAim();
        if (checkAim != null) {
            checkTaskDto.setAim(checkTask.getCheckAim().toString());
        }

        LocalDateTime lastFireDateTime = checkTask.getLastFireDateTime();
        if (lastFireDateTime != null) {
            checkTaskDto.setLast(lastFireDateTime.format(dateTimeFormatter));
        }

        LocalDateTime nextFireDateTime = checkTask.getNextFireDateTime();
        if (nextFireDateTime != null) {
            checkTaskDto.setNext(nextFireDateTime.format(dateTimeFormatter));
        }
    }

    @Override
    public void mapSpecificFields(CheckTaskDto checkTaskDto, CheckTask checkTask) {
        checkTask.setSite(new Site().setId(checkTaskDto.getSiteId()));

        String checkAim = checkTaskDto.getAim();
        if (checkAim != null) {
            checkTask.setCheckAim(CheckAim.valueOf(checkAim));
        }

        String lastFireDateTime = checkTaskDto.getLast();
        if (lastFireDateTime != null) {
            checkTask.setLastFireDateTime(LocalDateTime.parse(lastFireDateTime, dateTimeFormatter));
        }

        String nextFireDateTime = checkTaskDto.getNext();
        if (nextFireDateTime != null) {
            checkTask.setNextFireDateTime(LocalDateTime.parse(nextFireDateTime, dateTimeFormatter));
        }
    }
}
