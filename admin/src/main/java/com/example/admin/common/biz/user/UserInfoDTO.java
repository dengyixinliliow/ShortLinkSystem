package com.example.admin.common.biz.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoDTO {

    private String userId;

    private String username;

    private String realName;

    private String token;
}
