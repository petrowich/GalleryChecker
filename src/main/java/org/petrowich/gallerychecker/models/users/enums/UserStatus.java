package org.petrowich.gallerychecker.models.users.enums;

public enum UserStatus {
    ACTIVE (1, "active"),
    BANNED (2, "banned");

    private final int status;
    private final String name;

    UserStatus(int status, String name) {
        this.status = status;
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public static UserStatus toStatus(int status) {
        switch (status) {
            case 1:
                return UserStatus.ACTIVE;
            case 2:
                return UserStatus.BANNED;
            default:
                return null;
        }
    }
}
