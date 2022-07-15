package org.petrowich.gallerychecker.controllers.stored.galleries;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.petrowich.gallerychecker.dto.AbstractDto;
import org.petrowich.gallerychecker.models.master.sites.Site;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.petrowich.gallerychecker.services.galleries.StoredGalleryService;
import org.petrowich.gallerychecker.services.master.SiteService;
import org.petrowich.gallerychecker.services.master.TubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import static org.petrowich.gallerychecker.utils.HttpRequestHelper.parametersToString;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.PARTIAL_CONTENT;

@Log4j2
@Controller
@RequestMapping("/api/stored/galleries/")
public class StoredGalleryRestController {
    private static final String RETURN_TEMPLATE = "return {}";

    private final SiteService siteService;
    private final TubeService tubeService;
    private final StoredGalleryService storedGalleryService;

    @Autowired
    public StoredGalleryRestController(SiteService siteService,
                                       TubeService tubeService,
                                       StoredGalleryService storedGalleryService
    ) {
        this.siteService = siteService;
        this.tubeService = tubeService;
        this.storedGalleryService = storedGalleryService;
    }

    @PostMapping("reset")
    public ResponseEntity<TubeStoredGalleriesDto> reset(@RequestParam("siteId") Integer siteId,
                                                        @RequestParam("tubeId") Integer tubeId,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) {
        log.debug("{} '{}' {} export() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                parametersToString(request),
                request.getSession().getId());

        Site site = siteService.findById(siteId);
        Tube tube = tubeService.findById(tubeId);

        storedGalleryService.resetCheckedGalleries(site, tube);

        Optional<Tube> optionalTube = tubeService.findTubeWithStorageGalleriesStatisticsBySite(site, tube);

        if (optionalTube.isPresent()) {
            Tube tubeWithStoredGalleriesCounts = optionalTube.get();
            return createResponse(site, tubeWithStoredGalleriesCounts);
        }

        log.debug(RETURN_TEMPLATE, NOT_FOUND);
        return new ResponseEntity<>(new TubeStoredGalleriesDto(), NOT_FOUND);
    }

    private ResponseEntity<TubeStoredGalleriesDto> createResponse(Site site, Tube tube) {
        TubeStoredGalleriesDto tubeStoredGalleriesDto = new TubeStoredGalleriesDto();
        tubeStoredGalleriesDto.siteId = site.getId();
        tubeStoredGalleriesDto.tubeId = tube.getId();
        tubeStoredGalleriesDto.tubeHost = tube.getHost();
        tubeStoredGalleriesDto.storedGalleriesNumber = tube.getStoredGalleriesNumber();
        tubeStoredGalleriesDto.uncheckedStoredGalleriesNumber = tube.getUncheckedStoredGalleriesNumber();
        tubeStoredGalleriesDto.unavailableStoredGalleriesNumber = tube.getUnavailableStoredGalleriesNumber();

        log.debug(RETURN_TEMPLATE, PARTIAL_CONTENT);
        return new ResponseEntity<>(tubeStoredGalleriesDto, PARTIAL_CONTENT);
    }

    @Getter
    private static class TubeStoredGalleriesDto extends AbstractDto {
        private Integer siteId;
        private Integer tubeId;
        private String tubeHost;
        private Integer storedGalleriesNumber;
        private Integer uncheckedStoredGalleriesNumber;
        private Integer unavailableStoredGalleriesNumber;
    }
}
