package org.petrowich.gallerychecker.controllers.external.galleries;

import lombok.extern.log4j.Log4j2;
import org.petrowich.gallerychecker.mappers.external.TubeGalleryDtoMapper;
import org.petrowich.gallerychecker.models.galleries.TubeGallery;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.petrowich.gallerychecker.services.galleries.TubeGalleryService;
import org.petrowich.gallerychecker.services.master.TubeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.petrowich.gallerychecker.utils.HttpRequestHelper.parametersToString;
import static org.petrowich.gallerychecker.utils.HttpRequestHelper.parseNumberOrDefault;

@Log4j2
@Controller
@RequestMapping("/external/galleries/")
public class TubeGalleryController {
    private static final String TEXT_CONTENT_TYPE = "text/plain; charset=UTF-8";
    private static final String CHARACTER_ENCODING = "UTF-8";
    private static final String ATTRIBUTE_HOST = "host";
    private static final String ATTRIBUTE_PERIOD = "period";
    private static final String ATTRIBUTE_LIMIT = "limit";
    private static final Integer DEFAULT_LIMIT = 10_000;

    private final TubeService tubeService;
    private final TubeGalleryService tubeGalleryService;
    private final TubeGalleryDtoMapper tubeGalleryDtoMapper;

    public TubeGalleryController(TubeService tubeService,
                                 TubeGalleryService tubeGalleryService,
                                 TubeGalleryDtoMapper tubeGalleryDtoMapper) {
        this.tubeService = tubeService;
        this.tubeGalleryService = tubeGalleryService;
        this.tubeGalleryDtoMapper = tubeGalleryDtoMapper;
    }

    @GetMapping(value = "export/active")
    public void active(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.debug("{} '{}' {} active() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                parametersToString(request),
                request.getSession().getId());

        List<TubeGallery> galleries = collectGalleries(request, response, true);

        PrintWriter out = response.getWriter();
        out.println("#URL|#EMBED|#THUMB|#TITLE|#CHANNEL|#DURATION|#DATE|");
        galleries.stream()
                .map(tubeGalleryDtoMapper::toDto)
                .forEach(gallery -> out.println(
                        String.format("%s|%s|%s|%s|%s|%s|%s|",
                                gallery.getUrl(),
                                gallery.getEmbedCode(),
                                gallery.getThumbUrl(),
                                gallery.getDescription().replace("|", "!"),
                                gallery.getTags(),
                                gallery.getDuration(),
                                gallery.getDate()
                        )
                ));
    }

    @GetMapping(value = "export/deleted")
    public void deleted(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.debug("{} '{}' {} deleted() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                parametersToString(request),
                request.getSession().getId());

        List<TubeGallery> galleries = collectGalleries(request, response, false);

        PrintWriter out = response.getWriter();
        out.println("#URL|");
        galleries.forEach(gallery -> out.println(gallery.getUrl()));
    }

    private List<TubeGallery> collectGalleries(HttpServletRequest request,
                                               HttpServletResponse response,
                                               boolean active) throws IOException {
        String host = request.getParameter(ATTRIBUTE_HOST);
        Optional<Tube> optionalTube = tubeService.findByHost(host);
        Tube tube = optionalTube.orElseGet(() -> new Tube().setHost("not defined"));

        if (optionalTube.isEmpty()) {
            String message = String.format("tube host %s not found", host);
            log.error(message);
            response.sendError(SC_NOT_FOUND, message);
            return emptyList();
        }

        response.setContentType(TEXT_CONTENT_TYPE);
        response.setCharacterEncoding(CHARACTER_ENCODING);
        response.setStatus(SC_OK);

        Integer limit = parseNumberOrDefault(request.getParameter(ATTRIBUTE_LIMIT), DEFAULT_LIMIT);
        Integer period = parseNumberOrDefault(request.getParameter(ATTRIBUTE_PERIOD), -1);
        if (period > 0) {
            LocalDate date = LocalDate.now().minusDays(period);
            return tubeGalleryService.findByTubeByStatusFromDate(tube, active, date, limit);
        } else {
            return tubeGalleryService.findByTubeByStatus(tube, active, limit);
        }
    }
}
