package com.example.project.common.biz.user;

import com.example.project.common.constants.UserConstant;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLDecoder;

import static java.nio.charset.StandardCharsets.UTF_8;

public class UserTransmitFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String username = getHeader(httpServletRequest, UserConstant.USER_NAME_KEY, "username");
        String token = getHeader(httpServletRequest, UserConstant.USER_TOKEN_KEY, "token");
        if (StringUtils.hasText(username)) {
            username = URLDecoder.decode(username, UTF_8);
            String userId = getHeader(httpServletRequest, UserConstant.USER_ID_KEY, "userId");
            String realName = getHeader(httpServletRequest, UserConstant.REAL_NAME_KEY, "realName");
            if (StringUtils.hasText(realName)) {
                realName = URLDecoder.decode(realName, UTF_8);
            }
            UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                    .userId(userId)
                    .username(username)
                    .realName(realName)
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

    private String getHeader(HttpServletRequest request, String primaryName, String fallbackName) {
        String value = request.getHeader(primaryName);
        return StringUtils.hasText(value) ? value : request.getHeader(fallbackName);
    }
}
