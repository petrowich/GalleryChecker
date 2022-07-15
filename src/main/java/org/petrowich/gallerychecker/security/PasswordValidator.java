package org.petrowich.gallerychecker.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordValidator {

    private final PasswordEncoder passwordEncoder;

    public PasswordValidator(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String validatePassword(String newPassword, String newPassword2) {
        if (newPassword != null &&
                newPassword.length() != 0 &&
                newPassword.equals(newPassword2)) {
            return passwordEncoder.encode(newPassword);
        }
        return null;
    }
}
