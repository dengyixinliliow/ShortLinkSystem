package com.example.project.common.enums;

import java.util.Arrays;
import java.util.Objects;

public enum ValidDateTypeEnum {

    PERMANENT(0),
    CUSTOM(1);

    private final Integer type;

    ValidDateTypeEnum(Integer type) {
        this.type = type;
    }

    public Integer type() {
        return type;
    }

    public static boolean contains(Integer type) {
        return Arrays.stream(values())
                .anyMatch(each -> Objects.equals(each.type, type));
    }

    public static boolean isCustom(Integer type) {
        return Objects.equals(CUSTOM.type, type);
    }
}
