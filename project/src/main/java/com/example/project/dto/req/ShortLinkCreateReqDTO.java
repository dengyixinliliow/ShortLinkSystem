package com.example.project.dto.req;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShortLinkCreateReqDTO {

    private String domain;

    private String originUrl;

    private String favicon;

    private String gid;

    private Integer createdType;

    private Integer validDateType;

    private LocalDateTime validDate;

    private String describe;
}
