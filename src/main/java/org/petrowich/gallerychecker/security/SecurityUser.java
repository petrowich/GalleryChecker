package org.petrowich.gallerychecker.security;

import lombok.Data;
import org.petrowich.gallerychecker.models.users.UserInfo;
import org.petrowich.gallerychecker.models.users.enums.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class SecurityUser implements UserDetails {

    private final String username;
    private final String password;
    private final List<SimpleGrantedAuthority> authorities;
    private final boolean isActive;

    public SecurityUser(String username, String password, List<SimpleGrantedAuthority> authorities, boolean isActive) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.isActive = isActive;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>(authorities);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isActive;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    public static UserDetails fromUser(UserInfo userInfo) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(userInfo.getUsername())
                .password(userInfo.getPassword())
                .accountExpired(!userInfo.getUserStatus().equals(UserStatus.ACTIVE))
                .accountLocked(!userInfo.getUserStatus().equals(UserStatus.ACTIVE))
                .disabled(!userInfo.getUserStatus().equals(UserStatus.ACTIVE))
                .credentialsExpired(!userInfo.getUserStatus().equals(UserStatus.ACTIVE))
                .authorities(userInfo.getUserRole().getAuthorities())
                .roles(userInfo.getUserRole().name())
                .build();
    }
}
