package org.petrowich.gallerychecker.dto.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class AccountDto {
    private Integer userId;
    private String username;
    private String email;
}
