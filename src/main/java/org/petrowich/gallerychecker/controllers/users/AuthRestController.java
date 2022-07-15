package org.petrowich.gallerychecker.controllers.users;

import lombok.extern.log4j.Log4j2;
import org.petrowich.gallerychecker.dto.auth.PasswordDto;
import org.petrowich.gallerychecker.dto.ResponseDto;
import org.petrowich.gallerychecker.dto.enums.ToastType;
import org.petrowich.gallerychecker.models.users.UserInfo;
import org.petrowich.gallerychecker.models.users.enums.Permissions;
import org.petrowich.gallerychecker.models.users.enums.UserRole;
import org.petrowich.gallerychecker.security.PasswordValidator;
import org.petrowich.gallerychecker.services.auth.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

import static org.petrowich.gallerychecker.dto.enums.ToastType.ERROR;
import static org.petrowich.gallerychecker.dto.enums.ToastType.SUCCESS;
import static org.springframework.http.HttpStatus.*;


@Log4j2
@RestController
@RequestMapping("/api/auth")
public class AuthRestController {
    private static final String LOG_RETURN = "return {} {}";
    private final UserService userService;
    private final PasswordValidator passwordValidator;

    @Autowired
    public AuthRestController(UserService userService,
                              PasswordValidator passwordValidator) {
        this.userService = userService;
        this.passwordValidator = passwordValidator;
    }

    @PostMapping("/set-password")
    public ResponseEntity<ResponseDto> resetPassword(@RequestBody PasswordDto passwordDto,
                                                     HttpServletRequest request) {
        log.debug("{} '{}' resetPassword() session:{}",
                request.getMethod(),
                request.getRequestURI(),
                request.getSession().getId());


        String password = passwordValidator.validatePassword(passwordDto.getNewPassword(),
                passwordDto.getNewPassword2());
        if (password == null) {
            return createResponse("Error", "Incorrect password", ERROR, BAD_REQUEST);
        }

        UserInfo userInfo = userService.findById(passwordDto.getUserId());
        boolean isCurrent = request.getUserPrincipal().getName().equals(userInfo.getUsername());
        if (!isCurrent) {
            boolean isWriter = Arrays.stream(UserRole.values())
                    .filter(userRole -> userRole.getPermissions().contains(Permissions.USERS_WRITE))
                    .anyMatch(userRole -> request.isUserInRole(String.valueOf(userRole)));
            if (!isWriter) {
                return createResponse("Forbidden", "No permission to change password of another user", ERROR, BAD_REQUEST);
            }
        }

        userInfo.setPassword(password);
        try {
            userService.save(userInfo);
        } catch (Exception exception) {
            String message = exception.getMessage();
            log.error(message, exception);
            return createResponse("Error", message, ERROR, INTERNAL_SERVER_ERROR);
        }

        String message = String.format("username: %s", userInfo.getUsername());
        return createResponse("Password changed", message, SUCCESS, CREATED);
    }

    private ResponseEntity<ResponseDto> createResponse(String title, String message, ToastType toastType, HttpStatus status) {
        ResponseDto responseDto = new ResponseDto()
                .setToastType(toastType)
                .setTitle(title)
                .setMessage(message);
        log.debug(LOG_RETURN, status, message);
        return new ResponseEntity<>(responseDto, status);
    }
}
