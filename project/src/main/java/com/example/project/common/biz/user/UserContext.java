package com.example.project.common.biz.user;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.example.project.common.constants.UserConstant;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URLDecoder;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class UserContext {

    private static final ThreadLocal<UserInfoDTO> USER_THREAD_LOCAL = new TransmittableThreadLocal<>();

    private UserContext() {
    }

    public static void setUser(UserInfoDTO user) {
        USER_THREAD_LOCAL.set(user);
    }

    public static String getUserId() {
        UserInfoDTO userInfoDTO = USER_THREAD_LOCAL.get();
        return Optional.ofNullable(userInfoDTO)
                .map(UserInfoDTO::getUserId)
                .orElseGet(() -> getHeader(UserConstant.USER_ID_KEY, "userId"));
    }

    public static String getUsername() {
        UserInfoDTO userInfoDTO = USER_THREAD_LOCAL.get();
        return Optional.ofNullable(userInfoDTO)
                .map(UserInfoDTO::getUsername)
                .orElseGet(() -> decode(getHeader(UserConstant.USER_NAME_KEY, "username")));
    }

    public static String getRealName() {
        UserInfoDTO userInfoDTO = USER_THREAD_LOCAL.get();
        return Optional.ofNullable(userInfoDTO)
                .map(UserInfoDTO::getRealName)
                .orElseGet(() -> decode(getHeader(UserConstant.REAL_NAME_KEY, "realName")));
    }

    public static String getToken() {
        UserInfoDTO userInfoDTO = USER_THREAD_LOCAL.get();
        return Optional.ofNullable(userInfoDTO)
                .map(UserInfoDTO::getToken)
                .orElseGet(() -> getHeader(UserConstant.USER_TOKEN_KEY, "token"));
    }

    public static void removeUser() {
        USER_THREAD_LOCAL.remove();
    }

    private static String getHeader(String primaryName, String fallbackName) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        String value = request.getHeader(primaryName);
        return StringUtils.hasText(value) ? value : request.getHeader(fallbackName);
    }

    private static String decode(String value) {
        return StringUtils.hasText(value) ? URLDecoder.decode(value, UTF_8) : value;
    }
}
