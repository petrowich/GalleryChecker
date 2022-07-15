package org.petrowich.gallerychecker.controllers.stored.tasks;

import lombok.extern.log4j.Log4j2;
import org.petrowich.gallerychecker.dto.stored.CheckTaskDto;
import org.petrowich.gallerychecker.dto.stored.SiteDto;
import org.petrowich.gallerychecker.mappers.external.SiteDtoMapper;
import org.petrowich.gallerychecker.mappers.stored.CheckTaskDtoMapper;
import org.petrowich.gallerychecker.models.tasks.CheckTask;
import org.petrowich.gallerychecker.scheduler.exceptions.JobException;
import org.petrowich.gallerychecker.services.checks.CheckTaskService;
import org.petrowich.gallerychecker.services.master.SiteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

@Log4j2
@Controller
@RequestMapping("/stored/tasks")
public class CheckTaskController {
    private static final String ATTRIBUTE_ALL_TASKS = "allTasks";
    private static final String ATTRIBUTE_ALL_SITES = "allSites";
    private static final String ATTRIBUTE_TASK = "task";
    private static final String HTML_CONTENT_TYPE = "text/html; charset=UTF-8";
    private static final String REDIRECT_TO_TASKS = "redirect:/stored/tasks/";

    private final SiteService siteService;
    private final CheckTaskService checkTaskService;
    private final SiteDtoMapper siteDtoMapper;
    private final CheckTaskDtoMapper checkTaskDtoMapper;

    public CheckTaskController(SiteService siteService,
                               CheckTaskService checkTaskService,
                               SiteDtoMapper siteDtoMapper,
                               CheckTaskDtoMapper checkTaskDtoMapper) {
        this.siteService = siteService;
        this.checkTaskService = checkTaskService;
        this.siteDtoMapper = siteDtoMapper;
        this.checkTaskDtoMapper = checkTaskDtoMapper;
    }

    @GetMapping("")
    public String getAll(Model model, HttpServletRequest request, HttpServletResponse response) {
        log.debug("{} '{}' getAll() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getSession().getId());

        Collection<CheckTaskDto> checkTaskDtoList = checkTaskService.getAllTasks().stream()
                .map(checkTaskDtoMapper::toDto)
                .collect(Collectors.toList());
        model.addAttribute(ATTRIBUTE_ALL_TASKS, checkTaskDtoList);

        String template = "stored/tasks/tasks";
        response.setContentType(HTML_CONTENT_TYPE);
        log.debug("return template {} {}", template, HTML_CONTENT_TYPE);
        return template;
    }

    @GetMapping("/new")
    public String newTask(Model model, HttpServletRequest request, HttpServletResponse response) {
        log.debug("{} '{}' newTask() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getSession().getId());

        Collection<SiteDto> tubeDtoList = siteService.findAll().stream()
                .map(siteDtoMapper::toDto)
                .collect(Collectors.toList());
        model.addAttribute(ATTRIBUTE_ALL_SITES, tubeDtoList);
        model.addAttribute(ATTRIBUTE_TASK, new CheckTaskDto());

        String template = "stored/tasks/add-task :: add-task";
        response.setContentType(HTML_CONTENT_TYPE);
        log.debug("return template {} {}", template, HTML_CONTENT_TYPE);
        return template;
    }

    @PostMapping("/add")
    public String addTask(CheckTaskDto checkTaskDto, HttpServletRequest request, HttpServletResponse response) {
        log.debug("{} '{}' addTask() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getSession().getId());

        CheckTask checkTask = checkTaskDtoMapper.toModel(checkTaskDto);

        String username = request.getUserPrincipal().getName();
        checkTask.setUsername(username);

        try {
            checkTaskService.addTask(checkTask);
        } catch (JobException exception) {
            log.error(exception.getMessage());
        }

        response.setStatus(SC_CREATED);
        response.setContentType(HTML_CONTENT_TYPE);
        log.debug("return {} template {} {}", SC_CREATED, REDIRECT_TO_TASKS, HTML_CONTENT_TYPE);
        return REDIRECT_TO_TASKS;
    }

    @PostMapping("/cancel")
    public String cancelTask(@RequestParam("taskId") String taskId, HttpServletRequest request, HttpServletResponse response) {
        log.debug("{} '{}' taskId={} cancelTask() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                taskId,
                request.getSession().getId());

        log.info("Deleting task taskId:{}", taskId);
        Optional<CheckTask> optionalCheckTask = checkTaskService.getTaskByTriggerId(taskId);
        if (optionalCheckTask.isPresent()) {
            checkTaskService.cancelTask(optionalCheckTask.get());
            response.setStatus(SC_OK);
        } else {
            log.error("Task with id:{} not found", taskId);
            response.setStatus(SC_BAD_REQUEST);
        }

        log.debug("return {}", REDIRECT_TO_TASKS);
        return REDIRECT_TO_TASKS;
    }

    @PostMapping("/interrupt")
    public String interrupt(@RequestParam("taskId") String taskId, HttpServletRequest request, HttpServletResponse response) {
        log.debug("{} '{}' taskId={} interrupt() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                taskId,
                request.getSession().getId());

        if (taskId != null) {
            checkTaskService.interruptTask(taskId);
            response.setStatus(SC_OK);
        } else {
            log.error("Task id is missed");
            response.setStatus(SC_BAD_REQUEST);
        }

        log.debug("return {}", REDIRECT_TO_TASKS);
        return REDIRECT_TO_TASKS;
    }
}
