package org.petrowich.gallerychecker.controllers.stored.galleries;

import lombok.extern.log4j.Log4j2;
import org.petrowich.gallerychecker.dto.stored.StoredGalleryDto;
import org.petrowich.gallerychecker.mappers.stored.StoredGalleryDtoMapper;
import org.petrowich.gallerychecker.models.master.sites.Site;
import org.petrowich.gallerychecker.models.users.UserInfo;
import org.petrowich.gallerychecker.services.auth.UserService;
import org.petrowich.gallerychecker.services.master.SiteService;
import org.petrowich.gallerychecker.services.galleries.StoredGalleryService;
import org.petrowich.gallerychecker.processig.stored.upload.StoredGalleryUploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.SC_OK;

@Log4j2
@Controller
@RequestMapping("/stored/galleries/")
public class StoredGalleryController {
    private static final String ATTRIBUTE_ALL_GALLERIES = "allGalleries";
    private static final String ATTRIBUTE_HOST = "host";
    private static final String HTML_CONTENT_TYPE = "text/html; charset=UTF-8";
    private static final String TEXT_CONTENT_TYPE = "text/plain; charset=UTF-8";
    private static final String RETURN_TEMPLATE = "return {}";

    private final SiteService siteService;
    private final UserService userService;
    private final StoredGalleryService storedGalleryService;
    private final StoredGalleryUploader storedGalleryUploader;
    private final StoredGalleryDtoMapper storedGalleryDtoMapper;

    @Autowired
    public StoredGalleryController(SiteService siteService,
                                   UserService userService,
                                   StoredGalleryService storedGalleryService,
                                   StoredGalleryUploader storedGalleryUploader,
                                   StoredGalleryDtoMapper storedGalleryDtoMapper
    ) {
        this.siteService = siteService;
        this.userService = userService;
        this.storedGalleryService = storedGalleryService;
        this.storedGalleryUploader = storedGalleryUploader;
        this.storedGalleryDtoMapper = storedGalleryDtoMapper;
    }

    @GetMapping("")
    public String storedGalleries(Model model, HttpServletRequest request, HttpServletResponse response) {
        log.debug("{} {} storedGalleries() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getSession().getId());

        Collection<StoredGalleryDto> storedGalleryDtoList = storedGalleryService.findAll().stream()
                .map(storedGalleryDtoMapper::toDto)
                .collect(Collectors.toList());
        model.addAttribute(ATTRIBUTE_ALL_GALLERIES, storedGalleryDtoList);

        response.setContentType(HTML_CONTENT_TYPE);
        String template = "stored/galleries/stored-galleries";
        log.debug("return template {} {}", template, HTML_CONTENT_TYPE);
        return template;
    }

    @PostMapping("upload")
    public String upload(@RequestParam("siteId") Integer siteId,
                         @RequestParam("file") MultipartFile file,
                         HttpServletRequest request) {
        log.debug("{} {} file={}({}kB) upload() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                file.getName(),
                file.getSize() / 1024,
                request.getSession().getId());

        Site site = siteService.findById(siteId);
        String username = request.getUserPrincipal().getName();
        UserInfo user = userService.findByUsername(username);

        storedGalleryUploader.uploadFromFile(file, site, user);

        String template = "stored/galleries/stored-galleries";
        log.debug(RETURN_TEMPLATE, template);
        return template;
    }

    @GetMapping(value = "export/unavailable")
    public void unavailable(@RequestParam("site") String host,
                            HttpServletRequest request,
                            HttpServletResponse response) throws IOException {
        log.debug("{} {} unavailable() site={} session:{}",
                request.getMethod(),
                request.getRequestURI(),
                host,
                request.getSession().getId());
        log.info("Get unavailable stored galleries list");

        log.debug("Request parameter {}", request.getParameterMap());

        PrintWriter out = response.getWriter();
        out.println("#url");
        Site site = siteService.findByHost(host);
        storedGalleryService.getUnavailableGalleries(site).forEach(gallery -> out.println(gallery.getUrl()));

        response.setContentType(TEXT_CONTENT_TYPE);
        response.setStatus(SC_OK);
    }
}
