package org.petrowich.gallerychecker.models.users.enums;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.petrowich.gallerychecker.models.users.enums.Permissions.*;

public enum UserRole {
    ADMIN(1, "Admin", Set.of(USERS_WRITE, USERS_READ, TASKS_WRITE, TASKS_READ)),
    POWER_USER(2, "Power User", Set.of(USERS_READ, TASKS_WRITE, TASKS_READ)),
    USER(3, "User", Set.of(TASKS_READ));

    private final int role;
    private final String name;
    private final Set<Permissions> permissionsSet;

    UserRole(int role, String name, Set<Permissions> permissionsSet) {
        this.role = role;
        this.name = name;
        this.permissionsSet = permissionsSet;
    }

    public Set<Permissions> getPermissions() {
        return new HashSet<>(permissionsSet);
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        return getPermissions().stream()
                .map(permissions -> new SimpleGrantedAuthority(permissions.getPermission()))
                .collect(Collectors.toCollection(HashSet::new));
    }

    public String getName() {
        return name;
    }

    public int getRole() {
        return role;
    }

    public static UserRole toRole(int role) {
        switch (role) {
            case 1:
                return UserRole.ADMIN;
            case 2:
                return UserRole.POWER_USER;
            default:
                return UserRole.USER;
        }
    }
}
