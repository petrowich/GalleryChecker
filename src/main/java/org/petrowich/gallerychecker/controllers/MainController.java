package org.petrowich.gallerychecker.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Log4j2
@Controller
@RequestMapping("")
public class MainController {

    @GetMapping("")
    public String main(HttpServletRequest request) {
        log.debug("{} '{}' main() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getSession().getId());
        String redirect = "redirect:/external/tubes/";
        log.debug("return {}", redirect);
        return redirect;
    }
}
