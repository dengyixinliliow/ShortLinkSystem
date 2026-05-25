package com.example.project.common.constants;

import java.time.Duration;

public final class RedisKeyConstant {

    private RedisKeyConstant() {
    }

    public static final String GOTO_SHORT_LINK_KEY = "short-link:goto:%s";

    public static final String LOCK_GOTO_SHORT_LINK_KEY = "short-link:lock:goto:%s";

    public static final String SHORT_LINK_STATS_UV_KEY = "short-link:stats:uv:%s:%s:%s";

    public static final String SHORT_LINK_STATS_UIP_KEY = "short-link:stats:uip:%s:%s:%s";

    public static final Duration GOTO_SHORT_LINK_PERMANENT_TTL = Duration.ofDays(30);

    public static final Duration SHORT_LINK_STATS_UV_TTL = Duration.ofDays(2);

    public static final Duration SHORT_LINK_STATS_UIP_TTL = Duration.ofDays(2);
}
