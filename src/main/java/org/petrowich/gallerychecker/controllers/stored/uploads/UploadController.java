package org.petrowich.gallerychecker.controllers.stored.uploads;

import lombok.extern.log4j.Log4j2;
import org.petrowich.gallerychecker.dto.stored.UploadInfoDto;
import org.petrowich.gallerychecker.mappers.stored.UploadInfoDtoMapper;
import org.petrowich.gallerychecker.models.master.sites.Site;
import org.petrowich.gallerychecker.models.users.UserInfo;
import org.petrowich.gallerychecker.services.auth.UserService;
import org.petrowich.gallerychecker.services.master.SiteService;
import org.petrowich.gallerychecker.services.uploads.UploadInfoService;
import org.petrowich.gallerychecker.processig.stored.upload.StoredGalleryUploader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_OK;

@Log4j2
@Controller
@RequestMapping("/stored/uploads/")
public class UploadController {

    private static final String ATTRIBUTE_ALL_UPLOADS = "allUploads";
    private static final String HTML_CONTENT_TYPE = "text/html; charset=UTF-8";
    private static final String LOG_RETURN = "return template {} {}";

    private final SiteService siteService;
    private final UserService userService;
    private final UploadInfoService uploadInfoService;
    private final UploadInfoDtoMapper uploadInfoDtoMapper;
    private final StoredGalleryUploader storedGalleryUploader;

    public UploadController(SiteService siteService,
                            UserService userService,
                            UploadInfoService uploadInfoService,
                            UploadInfoDtoMapper uploadInfoDtoMapper,
                            StoredGalleryUploader storedGalleryUploader) {
        this.siteService = siteService;
        this.userService = userService;
        this.uploadInfoService = uploadInfoService;
        this.uploadInfoDtoMapper = uploadInfoDtoMapper;
        this.storedGalleryUploader = storedGalleryUploader;
    }

    @GetMapping("")
    public String getAll(Model model, HttpServletRequest request, HttpServletResponse response) {
        log.debug("{} '{}' getAll() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getSession().getId());

        Collection<UploadInfoDto> uploadInfoDtoList = uploadInfoService.findAll().stream()
                .map(uploadInfoDtoMapper::toDto)
                .collect(Collectors.toList());
        model.addAttribute(ATTRIBUTE_ALL_UPLOADS, uploadInfoDtoList);

        String template = "stored/uploads/uploads";
        response.setContentType(HTML_CONTENT_TYPE);
        response.setStatus(SC_OK);
        log.debug(LOG_RETURN, template, HTML_CONTENT_TYPE);
        return template;
    }

    @PostMapping("upload")
    public String upload(@RequestParam("siteId") Integer siteId,
                         @RequestParam("file") MultipartFile file,
                         HttpServletRequest request, HttpServletResponse response) {
        log.debug("{} {} file={}({}kB) upload() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                file.getName(),
                file.getSize()/1024,
                request.getSession().getId());

        Site site = siteService.findById(siteId);
        String username = request.getUserPrincipal().getName();
        UserInfo user = userService.findByUsername(username);

        storedGalleryUploader.uploadFromFile(file, site, user);

        String redirect = "redirect:/stored/uploads/";
        response.setStatus(SC_CREATED);
        response.setContentType(HTML_CONTENT_TYPE);
        log.debug("return {} template {} {}", SC_CREATED, redirect, HTML_CONTENT_TYPE);
        return redirect;
    }
}
