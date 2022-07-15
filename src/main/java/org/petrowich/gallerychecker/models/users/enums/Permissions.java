package org.petrowich.gallerychecker.models.users.enums;

public enum Permissions {
    USERS_READ("users:read"),
    USERS_WRITE("users:write"),
    TASKS_READ("tasks:read"),
    TASKS_WRITE("tasks:write");

    private final String permission;

    Permissions(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
