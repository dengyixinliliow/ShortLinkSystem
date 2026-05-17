package com.example.admin.common.enums;

import com.example.admin.common.convention.errorCode.IErrorCode;

public enum UserErrorCodeEnum implements IErrorCode {

  USER_NULL("B000200", "User record not found"),
  USER_NAME_EXIST("B000201", "Username already existed"),
  USER_EXIST("B000202", "User already existed"),
  USER_SAVE_ERROR("B000203", "User save error"),
  USER_UPDATE_ERROR("B000204", "User update error"),
  USER_LOGIN_ERROR("B000205", "Username or password error"),
  USER_ALREADY_LOGIN("B000206", "User already logged in"),
  USER_NOT_LOGIN("B000207", "User not logged in");

  private final String code;

  private final String message;

  UserErrorCodeEnum(String code, String message) {
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
