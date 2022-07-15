package org.petrowich.gallerychecker.controllers.stored.tasks;

import lombok.extern.log4j.Log4j2;
import org.petrowich.gallerychecker.dto.ResponseDto;
import org.petrowich.gallerychecker.dto.enums.ToastType;
import org.petrowich.gallerychecker.dto.stored.CheckTaskDto;
import org.petrowich.gallerychecker.mappers.stored.CheckTaskDtoMapper;
import org.petrowich.gallerychecker.models.checks.enums.CheckAim;
import org.petrowich.gallerychecker.models.master.sites.Site;
import org.petrowich.gallerychecker.models.tasks.CheckTask;
import org.petrowich.gallerychecker.services.checks.CheckTaskService;
import org.petrowich.gallerychecker.services.master.SiteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static org.petrowich.gallerychecker.dto.enums.ToastType.ERROR;
import static org.petrowich.gallerychecker.dto.enums.ToastType.SUCCESS;
import static org.springframework.http.HttpStatus.*;

@Log4j2
@RestController
@RequestMapping("/api/stored/tasks/")
public class CheckTaskRestController {
    private static final String TITLE_ERROR = "Error";
    private static final String LOG_RETURN = "return {} {}";
    private final CheckTaskDtoMapper checkTaskDtoMapper;
    private final CheckTaskService checkTaskService;
    private final SiteService siteService;

    public CheckTaskRestController(CheckTaskDtoMapper checkTaskDtoMapper,
                                   CheckTaskService checkTaskService,
                                   SiteService siteService) {
        this.checkTaskDtoMapper = checkTaskDtoMapper;
        this.checkTaskService = checkTaskService;
        this.siteService = siteService;
    }

    @PostMapping("add")
    public ResponseEntity<ResponseDto> createTask(@RequestBody CheckTaskDto checkTaskDto, HttpServletRequest request) {
        log.debug("{} '{}' createTask() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getSession().getId());
        log.trace(checkTaskDto);

        CheckTask checkTask = checkTaskDtoMapper.toModel(checkTaskDto);
        String username = request.getUserPrincipal().getName();
        checkTask.setUsername(username);

        CheckAim checkAim = checkTask.getCheckAim();
        Site site = siteService.findById(checkTaskDto.getSiteId());
        checkTask.setSite(site);

        if (checkAim == null) {
            return createResponse(TITLE_ERROR, "Check aim is not defined", ERROR, BAD_REQUEST);
        }

        if (site == null) {
            log.error("Site was not found by passed id {}", checkTaskDto.getSiteId());
        }

        String message = String.format("Details: check %s galleries of %s", checkAim.getShortName(), site);

        try {
            checkTaskService.addTask(checkTask);
            log.trace(checkTaskDto);
            return createResponse("Task created!", message, SUCCESS, CREATED);
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            message = String.format("%s%nError message: %s", message, exception.getMessage());
            return createResponse(TITLE_ERROR, message, ERROR, INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<ResponseDto> createResponse(String title,
                                                       String message,
                                                       ToastType toastType,
                                                       HttpStatus status) {
        ResponseDto responseDto = new ResponseDto()
                .setToastType(toastType)
                .setTitle(title)
                .setMessage(message);
        log.debug(LOG_RETURN, status, message);
        log.trace(responseDto);
        return new ResponseEntity<>(responseDto, status);
    }
}
