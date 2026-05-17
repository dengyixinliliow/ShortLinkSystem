package com.example.admin.common.constants;

import java.time.Duration;

public final class RedisCacheConstants {

    private RedisCacheConstants() {
    }

    public static final String LOCK_USER_REGISTER_KEY = "short-link:lock:user-register:";

    public static final String USER_LOGIN_KEY = "short-link:login:";

    public static final Duration USER_LOGIN_TOKEN_TTL = Duration.ofMinutes(30);
}
