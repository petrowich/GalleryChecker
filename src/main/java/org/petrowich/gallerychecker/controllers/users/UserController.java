package org.petrowich.gallerychecker.controllers.users;

import lombok.extern.log4j.Log4j2;
import org.petrowich.gallerychecker.controllers.users.exceptions.IncorrectUserPasswordException;
import org.petrowich.gallerychecker.dto.users.UserInfoDto;
import org.petrowich.gallerychecker.mappers.users.UserDtoMapper;
import org.petrowich.gallerychecker.models.users.UserInfo;
import org.petrowich.gallerychecker.models.users.enums.UserRole;
import org.petrowich.gallerychecker.models.users.enums.UserStatus;
import org.petrowich.gallerychecker.services.auth.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.SC_ACCEPTED;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;

@Log4j2
@Controller
@RequestMapping("/users")
public class UserController {
    private static final String HTML_CONTENT_TYPE = "text/html; charset=UTF-8";
    private static final String ATTRIBUTE_ALL_USERS = "allUsers";
    private static final String ATTRIBUTE_ALL_ROLES = "allRoles";
    private static final String ATTRIBUTE_ALL_STATUSES = "allStatuses";
    private static final String ATTRIBUTE_USER = "user";
    private static final String LOG_RETURN = "return template {} {}";

    private final UserService userService;
    private final UserDtoMapper userDtoMapper;
    private final SessionRegistry sessionRegistry;

    @Autowired
    public UserController(UserService userService,
                          UserDtoMapper userDtoMapper,
                          SessionRegistry sessionRegistry) {
        this.userService = userService;
        this.userDtoMapper = userDtoMapper;
        this.sessionRegistry = sessionRegistry;
    }

    @GetMapping("")
    public String getAll(Model model, HttpServletRequest request, HttpServletResponse response) {
        log.debug("{} '{}' getAll() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getSession().getId());

        List<String> loggedUserNames = sessionRegistry.getAllPrincipals().stream()
                .filter(principal -> !sessionRegistry.getAllSessions(principal, false).isEmpty())
                .map(UserDetails.class::cast)
                .map(UserDetails::getUsername)
                .distinct()
                .collect(Collectors.toList());

        List<UserInfoDto> userInfoDtoList = userService.findAll().stream()
                .map(userDtoMapper::toDto)
                .map(userInfoDto -> userInfoDto.setLoggedIn(loggedUserNames.contains(userInfoDto.getUsername())))
                .collect(Collectors.toList());

        model.addAttribute(ATTRIBUTE_ALL_USERS, userInfoDtoList);

        response.setContentType(HTML_CONTENT_TYPE);
        String template = "users/users";
        log.debug(LOG_RETURN, template, HTML_CONTENT_TYPE);
        return template;
    }

    @GetMapping("/new")
    public String newUser(Model model, HttpServletRequest request, HttpServletResponse response) {
        log.debug("{} '{}' newUser() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getSession().getId());

        model.addAttribute(ATTRIBUTE_ALL_ROLES, UserRole.values());
        model.addAttribute(ATTRIBUTE_USER, new UserInfoDto());

        String template = "users/add-user :: add-user";
        response.setContentType(HTML_CONTENT_TYPE);
        log.debug(LOG_RETURN, template, HTML_CONTENT_TYPE);
        return template;
    }

    @PostMapping("/add")
    public String addUser(UserInfoDto userInfoDto,
                          HttpServletRequest request,
                          HttpServletResponse response) throws IncorrectUserPasswordException {
        log.debug("{} '{}' addUser() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getSession().getId());
        log.trace(userInfoDto);

        UserInfo userInfo = userDtoMapper.toModel(userInfoDto);
        if (userInfo.getPassword() != null) {
            userService.save(userInfo);
        } else {
            throw new IncorrectUserPasswordException();
        }

        String redirect = "redirect:/users/";
        response.setStatus(SC_CREATED);
        response.setContentType(HTML_CONTENT_TYPE);
        log.debug("return {} template {} {}", SC_CREATED, redirect, HTML_CONTENT_TYPE);
        return redirect;
    }

    @GetMapping("/edit")
    public String editUser(@RequestParam("userId") Integer userId,
                           Model model,
                           HttpServletRequest request,
                           HttpServletResponse response) {
        log.debug("{} '{}' userId={} newUser() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                userId,
                request.getSession().getId());

        UserInfo userInfo = userService.findById(userId);
        UserInfoDto userInfoDto = userDtoMapper.toDto(userInfo);

        model.addAttribute(ATTRIBUTE_ALL_ROLES, UserRole.values());
        model.addAttribute(ATTRIBUTE_ALL_STATUSES, UserStatus.values());
        model.addAttribute(ATTRIBUTE_USER, userInfoDto);

        String template = "users/edit-user :: edit-user";
        response.setContentType(HTML_CONTENT_TYPE);
        log.debug(LOG_RETURN, template, HTML_CONTENT_TYPE);
        return template;
    }

    @PostMapping("/update")
    public String updateUser(UserInfoDto userInfoDto,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        log.debug("{} '{}' updateUser() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getSession().getId());
        log.trace(userInfoDto);

        UserInfo currentUserInfo = userService.findById(userInfoDto.getId());
        UserInfo editedUserInfo = userDtoMapper.toModel(userInfoDto).setPassword(currentUserInfo.getPassword());
        userService.save(editedUserInfo);

        String redirect = "redirect:/users/";
        response.setStatus(SC_ACCEPTED);
        response.setContentType(HTML_CONTENT_TYPE);
        log.debug("return {} template {} {}", SC_ACCEPTED, redirect, HTML_CONTENT_TYPE);
        return redirect;
    }

    @GetMapping("/user")
    public String user(Model model, HttpServletRequest request, HttpServletResponse response) {
        log.debug("{} '{}' account() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getSession().getId());

        String username = request.getUserPrincipal().getName();
        UserInfo userInfo = userService.findByUsername(username);
        UserInfoDto userInfoDto = userDtoMapper.toDto(userInfo);
        model.addAttribute(ATTRIBUTE_USER, userInfoDto);
        model.addAttribute(ATTRIBUTE_ALL_ROLES, UserRole.values());

        String template = "users/user";
        response.setContentType(HTML_CONTENT_TYPE);
        log.debug(LOG_RETURN, template, HTML_CONTENT_TYPE);
        return template;
    }
}
