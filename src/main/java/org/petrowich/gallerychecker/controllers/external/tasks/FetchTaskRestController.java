package org.petrowich.gallerychecker.controllers.external.tasks;

import lombok.extern.log4j.Log4j2;
import org.petrowich.gallerychecker.dto.external.FetchTaskDto;
import org.petrowich.gallerychecker.dto.ResponseDto;
import org.petrowich.gallerychecker.mappers.external.FetchTaskDtoMapper;
import org.petrowich.gallerychecker.models.tasks.FetchTask;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.petrowich.gallerychecker.models.fetches.enums.FetchAim;
import org.petrowich.gallerychecker.dto.enums.ToastType;
import org.petrowich.gallerychecker.models.users.UserInfo;
import org.petrowich.gallerychecker.scheduler.exceptions.JobException;
import org.petrowich.gallerychecker.scheduler.external.exceptions.FetchJobException;
import org.petrowich.gallerychecker.services.auth.UserService;
import org.petrowich.gallerychecker.services.fetches.FetchTaskService;
import org.petrowich.gallerychecker.services.master.TubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static org.petrowich.gallerychecker.dto.enums.ToastType.ERROR;
import static org.petrowich.gallerychecker.dto.enums.ToastType.SUCCESS;
import static org.springframework.http.HttpStatus.*;

@Log4j2
@RestController
@RequestMapping("/api/external/tasks/")
public class FetchTaskRestController {
    private static final String TITLE_ERROR = "Error";
    private static final String LOG_RETURN = "return {} {}";
    private final FetchTaskDtoMapper fetchTaskDtoMapper;
    private final FetchTaskService fetchTaskService;
    private final TubeService tubeService;

    @Autowired
    public FetchTaskRestController(FetchTaskDtoMapper fetchTaskDtoMapper,
                                   FetchTaskService fetchTaskService,
                                   TubeService tubeService) {
        this.fetchTaskDtoMapper = fetchTaskDtoMapper;
        this.fetchTaskService = fetchTaskService;
        this.tubeService = tubeService;
    }

    @PostMapping("add")
    public ResponseEntity<ResponseDto> createTask(@RequestBody FetchTaskDto fetchTaskDto, HttpServletRequest request) {
        log.debug("{} '{}' createTask() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getSession().getId());
        log.trace(fetchTaskDto);

        FetchTask fetchTask = fetchTaskDtoMapper.toModel(fetchTaskDto);
        String username = request.getUserPrincipal().getName();
        fetchTask.setUsername(username);

        FetchAim fetchAim = fetchTask.getFetchAim();
        Tube tube = tubeService.findById(fetchTaskDto.getTubeId());
        fetchTask.setTube(tube);

        if (fetchAim == null) {
            return createResponse(TITLE_ERROR, "Fetch aim is not defined", ERROR, BAD_REQUEST);
        }

        if (tube == null) {
            log.error("Tube was not found by passed id {}", fetchTaskDto.getTubeId());
            return createResponse(TITLE_ERROR, "Tube was not found", ERROR, BAD_REQUEST);
        }

        String message = String.format("Details: %s galleries from %s", fetchAim.getShortName(), tube.getHost());

        try {
            fetchTaskService.addTask(fetchTask);
            log.trace(fetchTaskDto);
            return createResponse("Task created!", message, SUCCESS, CREATED);
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            message = String.format("%s%nError message: %s", message, exception.getMessage());
            return createResponse(TITLE_ERROR, message, ERROR, INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<ResponseDto> createResponse(String title, String message, ToastType toastType, HttpStatus status) {
        ResponseDto responseDto = new ResponseDto()
                .setToastType(toastType)
                .setTitle(title)
                .setMessage(message);
        log.debug(LOG_RETURN, status, message);
        log.trace(responseDto);
        return new ResponseEntity<>(responseDto, status);
    }
}
