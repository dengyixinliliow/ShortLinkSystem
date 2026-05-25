package com.example.gateway.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class UserTokenValidateFilter implements GlobalFilter, Ordered {

    private static final String USER_LOGIN_KEY = "short-link:login:";

    private static final String USER_ID_KEY = "X-User-Id";

    private static final String USER_NAME_KEY = "username";

    private static final String TRANSMIT_USER_NAME_KEY = "X-User-Name";

    private static final String REAL_NAME_KEY = "X-Real-Name";

    private static final String USER_TOKEN_KEY = "token";

    private static final String TRANSMIT_USER_TOKEN_KEY = "X-User-Token";

    private static final String USER_NOT_LOGIN_RESPONSE = """
            {"code":"B000207","message":"User not logged in","data":null,"requestId":null,"success":false}
            """;

    private final ReactiveStringRedisTemplate stringRedisTemplate;

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (isPublicRequest(request)) {
            return chain.filter(exchange);
        }

        String username = request.getHeaders().getFirst(USER_NAME_KEY);
        String token = request.getHeaders().getFirst(USER_TOKEN_KEY);
        if (!StringUtils.hasText(username) || !StringUtils.hasText(token)) {
            return unauthorized(exchange);
        }

        String decodedUsername = URLDecoder.decode(username, StandardCharsets.UTF_8);
        String redisKey = USER_LOGIN_KEY + decodedUsername;
        return stringRedisTemplate.opsForHash()
                .get(redisKey, token)
                .cast(String.class)
                .flatMap(userJson -> filterWithUserInfo(exchange, chain, decodedUsername, token, userJson)
                        .thenReturn(Boolean.TRUE))
                .switchIfEmpty(Mono.defer(() -> unauthorized(exchange).thenReturn(Boolean.FALSE)))
                .then();
    }

    private Mono<Void> filterWithUserInfo(ServerWebExchange exchange,
                                          GatewayFilterChain chain,
                                          String username,
                                          String token,
                                          String userJson) {
        try {
            JsonNode userInfo = objectMapper.readTree(userJson);
            String userId = userInfo.path("id").asText();
            String realName = userInfo.path("realName").asText();
            ServerHttpRequest request = exchange.getRequest()
                    .mutate()
                    .headers(headers -> {
                        headers.set(USER_ID_KEY, encode(userId));
                        headers.set(TRANSMIT_USER_NAME_KEY, encode(username));
                        headers.set(REAL_NAME_KEY, encode(realName));
                        headers.set(TRANSMIT_USER_TOKEN_KEY, token);
                    })
                    .build();
            return chain.filter(exchange.mutate().request(request).build());
        } catch (Exception ex) {
            return unauthorized(exchange);
        }
    }

    private boolean isPublicRequest(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();
        if ("/not-found".equals(path) || !path.startsWith("/api/")) {
            return true;
        }
        if (HttpMethod.POST.equals(method) && "/api/shortlink/admin/v1/user/login".equals(path)) {
            return true;
        }
        if (HttpMethod.POST.equals(method) && "/api/shortlink/admin/v1/user".equals(path)) {
            return true;
        }
        return HttpMethod.GET.equals(method) && path.startsWith("/api/shortlink/admin/v1/user/has-username/");
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(USER_NOT_LOGIN_RESPONSE.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
