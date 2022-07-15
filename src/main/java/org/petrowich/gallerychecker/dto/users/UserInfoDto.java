package org.petrowich.gallerychecker.dto.users;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.petrowich.gallerychecker.dto.AbstractDto;

@Setter
@Getter
@Accessors(chain = true)
public class UserInfoDto extends AbstractDto {
    private Integer id;
    private String username;
    private String email;
    private String roleName;
    private String role;
    private String statusName;
    private String status;
    private String newPassword;
    private String newPassword2;
    private boolean isLoggedIn;
}
