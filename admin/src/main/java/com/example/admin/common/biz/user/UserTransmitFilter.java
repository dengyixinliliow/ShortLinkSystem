package com.example.admin.common.biz.user;

import com.example.admin.common.constants.RedisCacheConstants;
import com.example.admin.common.constants.UserConstant;
import com.example.admin.common.convention.result.Results;
import com.example.admin.common.enums.UserErrorCodeEnum;
import com.example.admin.dao.entity.UserDO;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLDecoder;

import static java.nio.charset.StandardCharsets.UTF_8;

public class UserTransmitFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;

    public UserTransmitFilter(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        String username = httpServletRequest.getHeader(UserConstant.USER_NAME_KEY);
        String token = httpServletRequest.getHeader(UserConstant.USER_TOKEN_KEY);
        if (StringUtils.hasText(username) && StringUtils.hasText(token)) {
            if (StringUtils.hasText(username)) {
                username = URLDecoder.decode(username, UTF_8);
            }
            Object userJson = stringRedisTemplate.opsForHash()
                    .get(RedisCacheConstants.USER_LOGIN_KEY + username, token);
            if (userJson == null) {
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpServletResponse.setContentType("application/json;charset=UTF-8");
                httpServletResponse.getWriter().write(JSONUtil.toJsonStr(Results.failure(
                        UserErrorCodeEnum.USER_NOT_LOGIN.code(),
                        UserErrorCodeEnum.USER_NOT_LOGIN.message())));
                return;
            }
            UserDO userDO = JSONUtil.toBean(userJson.toString(), UserDO.class);
            UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                    .userId(String.valueOf(userDO.getId()))
                    .username(userDO.getUsername())
                    .realName(userDO.getRealName())
                    .token(token)
                    .build();
            UserContext.setUser(userInfoDTO);
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }
}
