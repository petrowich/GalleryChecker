package org.petrowich.gallerychecker.dto.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class PasswordDto {
    private Integer userId;
    private String newPassword;
    private String newPassword2;
}
