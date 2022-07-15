package org.petrowich.gallerychecker.controllers.external.tubes;

import lombok.extern.log4j.Log4j2;
import org.petrowich.gallerychecker.dto.external.FetchTaskDto;
import org.petrowich.gallerychecker.dto.external.TubeDto;
import org.petrowich.gallerychecker.mappers.external.TubeDtoMapper;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.petrowich.gallerychecker.scheduler.JobRepository;
import org.petrowich.gallerychecker.services.master.TubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.SC_OK;

@Log4j2
@Controller
@RequestMapping("/external/tubes")
public class TubeController {
    private static final String ATTRIBUTE_ALL_TUBES = "allTubes";
    private static final String ATTRIBUTE_HOST = "host";
    private static final String ATTRIBUTE_TASK = "task";
    private static final String HTML_CONTENT_TYPE = "text/html; charset=UTF-8";
    private static final String LOG_RETURN = "return template {} {}";

    private final TubeService tubeService;
    private final TubeDtoMapper tubeDtoMapper;
    private final JobRepository jobRepository;

    @Autowired
    public TubeController(TubeService tubeService, TubeDtoMapper tubeDtoMapper, JobRepository jobRepository) {
        this.tubeService = tubeService;
        this.tubeDtoMapper = tubeDtoMapper;
        this.jobRepository = jobRepository;
    }

    @GetMapping("")
    public String getAll(Model model, HttpServletRequest request, HttpServletResponse response) {
        log.debug("{} '{}' getAll() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getSession().getId());

        Collection<TubeDto> tubeDtoList = tubeService.findAllWithTubeGalleriesStatistics().stream()
                .filter(jobRepository::isJobExist)
                .map(tubeDtoMapper::toDto)
                .map(tubeDto -> tubeDto.setFetchJobExists(true))
                .collect(Collectors.toList());
        model.addAttribute(ATTRIBUTE_ALL_TUBES, tubeDtoList);

        String template = "external/tubes/tubes";
        response.setContentType(HTML_CONTENT_TYPE);
        log.debug(LOG_RETURN, template, HTML_CONTENT_TYPE);
        return template;
    }

    @GetMapping("import")
    public String newImport(@RequestParam("tubeId") Integer tubeId,
                            Model model,
                            HttpServletRequest request,
                            HttpServletResponse response) {
        log.debug("{} '{}' tubeId={} newImport() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                tubeId,
                request.getSession().getId());

        Tube tube = tubeService.findById(tubeId);
        FetchTaskDto fetchTaskDto = new FetchTaskDto()
                .setTubeId(tubeId)
                .setHost(tube.getHost())
                .setOnce(true);
        model.addAttribute(ATTRIBUTE_TASK, fetchTaskDto);

        response.setStatus(SC_OK);
        response.setContentType(HTML_CONTENT_TYPE);
        String template = "external/tubes/import-galleries :: import-galleries";
        log.debug(LOG_RETURN, template, HTML_CONTENT_TYPE);
        return template;
    }

    @GetMapping("export")
    public String export(@RequestParam("tubeId") Integer tubeId,
                            Model model,
                            HttpServletRequest request,
                            HttpServletResponse response) {
        log.debug("{} '{}' tubeId={} export() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                tubeId,
                request.getSession().getId());

        Tube tube = tubeService.findById(tubeId);
        model.addAttribute(ATTRIBUTE_HOST, tube.getHost());

        response.setStatus(SC_OK);
        response.setContentType(HTML_CONTENT_TYPE);
        String template = "external/tubes/export-galleries :: export-galleries";
        log.debug(LOG_RETURN, template, HTML_CONTENT_TYPE);
        return template;
    }
}
