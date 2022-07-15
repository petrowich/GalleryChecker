package org.petrowich.gallerychecker.controllers.stored;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Log4j2
@Controller
@RequestMapping("/stored")
public class StoredController {

    @GetMapping("")
    public String stored(HttpServletRequest request) {
        log.debug("{} '{}' stored() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getSession().getId());
        String redirect = "redirect:/stored/sites/";
        log.debug("return {}", redirect);
        return redirect;
    }
}
