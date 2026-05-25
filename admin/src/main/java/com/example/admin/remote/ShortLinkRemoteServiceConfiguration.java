package com.example.admin.remote;

import com.example.admin.common.biz.user.UserContext;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public class ShortLinkRemoteServiceConfiguration {

    @Bean
    public RequestInterceptor userContextRequestInterceptor() {
        return template -> {
            String username = UserContext.getUsername();
            String token = UserContext.getToken();
            if (username != null) {
                template.header("username", username);
            }
            if (token != null) {
                template.header("token", token);
            }
        };
    }
}
