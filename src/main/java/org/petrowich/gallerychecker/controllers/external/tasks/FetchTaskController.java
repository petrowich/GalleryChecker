package org.petrowich.gallerychecker.controllers.external.tasks;


import lombok.extern.log4j.Log4j2;
import org.petrowich.gallerychecker.dto.external.FetchTaskDto;
import org.petrowich.gallerychecker.dto.external.TubeDto;
import org.petrowich.gallerychecker.mappers.external.FetchTaskDtoMapper;
import org.petrowich.gallerychecker.mappers.external.TubeDtoMapper;
import org.petrowich.gallerychecker.models.tasks.FetchTask;
import org.petrowich.gallerychecker.scheduler.JobRepository;
import org.petrowich.gallerychecker.scheduler.exceptions.JobException;
import org.petrowich.gallerychecker.services.fetches.FetchTaskService;
import org.petrowich.gallerychecker.services.master.TubeService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.Optional;

import static javax.servlet.http.HttpServletResponse.SC_CREATED;

@Log4j2
@Controller
@RequestMapping("/external/tasks")
public class FetchTaskController {
    private static final String ATTRIBUTE_ALL_TASKS = "allTasks";
    private static final String ATTRIBUTE_ALL_TUBES = "allTubes";
    private static final String ATTRIBUTE_TASK = "task";
    private static final String HTML_CONTENT_TYPE = "text/html; charset=UTF-8";

    private final FetchTaskService fetchTaskService;
    private final TubeService tubeService;
    private final FetchTaskDtoMapper fetchTaskDtoMapper;
    private final TubeDtoMapper tubeDtoMapper;
    private final JobRepository jobRepository;

    public FetchTaskController(FetchTaskService fetchTaskService,
                               TubeService tubeService,
                               FetchTaskDtoMapper fetchTaskDtoMapper,
                               TubeDtoMapper tubeDtoMapper,
                               JobRepository jobRepository) {
        this.fetchTaskService = fetchTaskService;
        this.tubeService = tubeService;
        this.fetchTaskDtoMapper = fetchTaskDtoMapper;
        this.tubeDtoMapper = tubeDtoMapper;
        this.jobRepository = jobRepository;
    }

    @GetMapping("")
    public String getAll(Model model, HttpServletRequest request, HttpServletResponse response) {
        log.debug("{} '{}' getAll() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getSession().getId());

        Collection<FetchTaskDto> fetchTaskDtoList = fetchTaskService.getAllTasks().stream()
                .map(fetchTaskDtoMapper::toDto)
                .collect(Collectors.toList());
        model.addAttribute(ATTRIBUTE_ALL_TASKS, fetchTaskDtoList);

        String template = "external/tasks/tasks";
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

        Collection<TubeDto> tubeDtoList = tubeService.findAll().stream()
                .filter(jobRepository::isJobExist)
                .map(tubeDtoMapper::toDto)
                .collect(Collectors.toList());
        model.addAttribute(ATTRIBUTE_ALL_TUBES, tubeDtoList);
        model.addAttribute(ATTRIBUTE_TASK, new FetchTaskDto());

        String template = "external/tasks/add-task :: add-task";
        response.setContentType(HTML_CONTENT_TYPE);
        log.debug("return template {} {}", template, HTML_CONTENT_TYPE);
        return template;
    }

    @PostMapping("/add")
    public String addTask(FetchTaskDto fetchTaskDto, HttpServletRequest request, HttpServletResponse response)
            throws JobException {
        log.debug("{} '{}' addTask() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getSession().getId());

        FetchTask fetchTask = fetchTaskDtoMapper.toModel(fetchTaskDto);

        String username = request.getUserPrincipal().getName();
        fetchTask.setUsername(username);

        fetchTaskService.addTask(fetchTask);

        String redirect = "redirect:/external/tasks/";
        response.setStatus(SC_CREATED);
        response.setContentType(HTML_CONTENT_TYPE);
        log.debug("return {} template {} {}", SC_CREATED, redirect, HTML_CONTENT_TYPE);
        return redirect;
    }

    @PostMapping("/cancel")
    public String cancelTask(@RequestParam("taskId") String taskId, HttpServletRequest request, HttpServletResponse response) {
        log.debug("{} '{}' taskId={} cancelTask() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                taskId,
                request.getSession().getId());

        log.info("Deleting task taskId:{}", taskId);
        Optional<FetchTask> optionalFetchTask = fetchTaskService.getTaskByTriggerId(taskId);
        if (optionalFetchTask.isPresent()) {
            fetchTaskService.cancelTask(optionalFetchTask.get());
            response.setStatus(HttpStatus.OK.value());
        } else {
            log.error("Task with id:{} not found", taskId);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }

        String redirect = "redirect:/external/tasks/";
        log.debug("return {}", redirect);
        return redirect;
    }
}
