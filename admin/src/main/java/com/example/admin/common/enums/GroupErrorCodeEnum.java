package com.example.admin.common.enums;

import com.example.admin.common.convention.errorCode.IErrorCode;

public enum GroupErrorCodeEnum implements IErrorCode {

    GROUP_NAME_NULL("B000300", "Group name cannot be null"),
    GROUP_GID_NULL("B000301", "Group gid cannot be null"),
    GROUP_NOT_FOUND("B000302", "Group not found");

    private final String code;

    private final String message;

    GroupErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
