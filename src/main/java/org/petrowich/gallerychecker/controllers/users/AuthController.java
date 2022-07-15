package org.petrowich.gallerychecker.controllers.users;

import lombok.extern.log4j.Log4j2;
import org.petrowich.gallerychecker.dto.auth.AccountDto;
import org.petrowich.gallerychecker.dto.auth.PasswordDto;
import org.petrowich.gallerychecker.models.users.UserInfo;
import org.petrowich.gallerychecker.services.auth.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.*;

@Log4j2
@Controller
@RequestMapping("/auth")
public class AuthController {
    private static final String HTML_CONTENT_TYPE = "text/html; charset=UTF-8";
    private static final String ATTRIBUTE_ACCOUNT = "account";
    private static final String ATTRIBUTE_PASSWORD = "password";
    private static final String LOG_RETURN = "return {} {}";

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response) {
        log.debug("{} '{}' login() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getSession().getId());

        response.setContentType(HTML_CONTENT_TYPE);
        String template = "auth/login";
        log.debug(LOG_RETURN, template, HTML_CONTENT_TYPE);
        return template;
    }

    @GetMapping("/success")
    public String success(HttpServletRequest request) {
        log.debug("{} '{}' success() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getSession().getId());
        String redirect = "redirect:/external/";
        log.debug("return {}", redirect);
        return redirect;
    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam("userId") Integer userId,
                                Model model,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        log.debug("{} '{}' userId={} newUser() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                userId,
                request.getSession().getId());

        model.addAttribute(ATTRIBUTE_PASSWORD, new PasswordDto().setUserId(userId));
        String template = "auth/reset-password :: reset-password";
        response.setContentType(HTML_CONTENT_TYPE);
        log.debug(LOG_RETURN, template, HTML_CONTENT_TYPE);
        return template;
    }

    @GetMapping("/account")
    public String account(Model model, HttpServletRequest request, HttpServletResponse response) {
        log.debug("{} '{}' account() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getSession().getId());

        String username = request.getUserPrincipal().getName();
        UserInfo userInfo = userService.findByUsername(username);
        AccountDto accountDto = new AccountDto()
                .setUserId(userInfo.getId())
                .setUsername(userInfo.getUsername())
                .setEmail(userInfo.getEmail());
        model.addAttribute(ATTRIBUTE_ACCOUNT, accountDto);

        String template = "auth/account";
        response.setContentType(HTML_CONTENT_TYPE);
        log.debug(LOG_RETURN, template, HTML_CONTENT_TYPE);
        return template;
    }

    @PostMapping("/update")
    public String updateAccount(AccountDto accountDto,
                             HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        log.debug("{} '{}' updateUser() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getSession().getId());
        log.trace(accountDto);

        UserInfo userInfo = userService.findById(accountDto.getUserId());
        String currentUserName = request.getUserPrincipal().getName();
        boolean isCurrent = currentUserName.equals(userInfo.getUsername());
        int httpStatus;
        if (isCurrent) {
            userInfo.setEmail(accountDto.getEmail());
            userService.save(userInfo);
            httpStatus = SC_ACCEPTED;
        } else {
            log.error("attempt to update another user's account");
            httpStatus = SC_FORBIDDEN;
        }

        String redirect = "redirect:account/";
        response.setContentType(HTML_CONTENT_TYPE);
        log.debug("return {} template {} {}", httpStatus, redirect, HTML_CONTENT_TYPE);
        return redirect;
    }
}
