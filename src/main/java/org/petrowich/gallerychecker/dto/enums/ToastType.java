package org.petrowich.gallerychecker.dto.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ToastType {
    SUCCESS("success"),
    INFO("info"),
    WARNING("warning"),
    ERROR("error");

    @JsonValue
    private final String type;

    ToastType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
