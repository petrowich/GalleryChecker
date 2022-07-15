package org.petrowich.gallerychecker.controllers.stored.sites;

import lombok.extern.log4j.Log4j2;
import org.petrowich.gallerychecker.dto.external.TubeDto;
import org.petrowich.gallerychecker.dto.stored.CheckTaskDto;
import org.petrowich.gallerychecker.dto.stored.SiteDto;
import org.petrowich.gallerychecker.mappers.external.SiteDtoMapper;
import org.petrowich.gallerychecker.mappers.external.TubeDtoMapper;
import org.petrowich.gallerychecker.models.master.sites.Site;
import org.petrowich.gallerychecker.services.master.SiteService;
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

import static javax.servlet.http.HttpServletResponse.*;

@Log4j2
@Controller
@RequestMapping("/stored/sites/")
public class SiteController {
    private static final String ATTRIBUTE_ALL_SITES = "allSites";
    private static final String ATTRIBUTE_HOST = "host";
    private static final String ATTRIBUTE_SITE = "site";
    private static final String ATTRIBUTE_TASK = "task";
    private static final String ATTRIBUTE_SITE_TUBES = "siteTubes";
    private static final String HTML_CONTENT_TYPE = "text/html; charset=UTF-8";
    private static final String LOG_RETURN = "return template {} {}";

    private final SiteService siteService;
    private final TubeService tubeService;
    private final SiteDtoMapper siteDtoMapper;
    private final TubeDtoMapper tubeDtoMapper;

    @Autowired
    public SiteController(SiteService siteService,
                          TubeService tubeService,
                          SiteDtoMapper siteDtoMapper,
                          TubeDtoMapper tubeDtoMapper) {
        this.siteService = siteService;
        this.tubeService = tubeService;
        this.siteDtoMapper = siteDtoMapper;
        this.tubeDtoMapper = tubeDtoMapper;
    }

    @GetMapping("")
    public String getAll(Model model, HttpServletRequest request, HttpServletResponse response) {
        log.debug("{} '{}' getAll() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getSession().getId());

        Collection<SiteDto> siteDtoList = siteService.findAllWithStatistics().stream()
                .map(siteDtoMapper::toDto)
                .collect(Collectors.toList());
        model.addAttribute(ATTRIBUTE_ALL_SITES, siteDtoList);

        response.setStatus(SC_OK);
        response.setContentType(HTML_CONTENT_TYPE);
        String template = "stored/sites/sites";
        log.debug(LOG_RETURN, template, HTML_CONTENT_TYPE);
        return template;
    }

    @GetMapping("tubes")
    public String tubes(@RequestParam("siteId") Integer siteId,
                        Model model,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        log.debug("{} '{}' siteId={} tubes() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                siteId,
                request.getSession().getId());

        Site site = siteService.findById(siteId);
        Collection<TubeDto> tubeDtoList = tubeService.findAllWithStorageGalleriesStatisticsBySite(site).stream()
                .map(tubeDtoMapper::toDto)
                .map(tubeDto -> tubeDto.setFetchJobExists(true))
                .collect(Collectors.toList());
        SiteDto siteDto = siteDtoMapper.toDto(site);
        model.addAttribute(ATTRIBUTE_SITE, siteDto);
        model.addAttribute(ATTRIBUTE_SITE_TUBES, tubeDtoList);

        response.setStatus(SC_OK);
        response.setContentType(HTML_CONTENT_TYPE);
        String template = "stored/sites/site-tubes :: site-tubes";
        log.debug(LOG_RETURN, template, HTML_CONTENT_TYPE);
        return template;
    }

    @GetMapping("upload")
    public String upload(@RequestParam("siteId") Integer siteId,
                        Model model,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        log.debug("{} '{}' siteId={} upload() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                siteId,
                request.getSession().getId());

        Site site = siteService.findById(siteId);
        SiteDto siteDto = siteDtoMapper.toDto(site);
        model.addAttribute(ATTRIBUTE_SITE, siteDto);

        response.setStatus(SC_OK);
        response.setContentType(HTML_CONTENT_TYPE);
        String template = "stored/sites/upload-galleries :: upload-galleries";
        log.debug(LOG_RETURN, template, HTML_CONTENT_TYPE);
        return template;
    }

    @GetMapping("check")
    public String check(@RequestParam("siteId") Integer siteId,
                         Model model,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        log.debug("{} '{}' siteId={} check() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                siteId,
                request.getSession().getId());

        Site site = siteService.findByIdWithStatistics(siteId);
        SiteDto siteDto = siteDtoMapper.toDto(site);
        model.addAttribute(ATTRIBUTE_SITE, siteDto);

        CheckTaskDto checkTaskDto = new CheckTaskDto()
                .setSiteId(siteId)
                .setHost(site.getHost())
                .setOnce(true);
        model.addAttribute(ATTRIBUTE_TASK, checkTaskDto);

        response.setStatus(SC_OK);
        response.setContentType(HTML_CONTENT_TYPE);
        String template = "stored/sites/check-galleries :: check-galleries";
        log.debug(LOG_RETURN, template, HTML_CONTENT_TYPE);
        return template;
    }

    @GetMapping("export")
    public String export(@RequestParam("siteId") Integer siteId,
                            Model model,
                            HttpServletRequest request,
                            HttpServletResponse response) {
        log.debug("{} '{}' siteId={} export() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                siteId,
                request.getSession().getId());

        Site site = siteService.findById(siteId);
        model.addAttribute(ATTRIBUTE_HOST, site.getHost());

        response.setStatus(SC_OK);
        response.setContentType(HTML_CONTENT_TYPE);
        String template = "stored/sites/export-galleries :: export-galleries";
        log.debug(LOG_RETURN, template, HTML_CONTENT_TYPE);
        return template;
    }
}
