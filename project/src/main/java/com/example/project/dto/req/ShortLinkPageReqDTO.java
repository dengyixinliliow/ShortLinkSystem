package com.example.project.dto.req;

import lombok.Data;

@Data
public class ShortLinkPageReqDTO {

    private Long current = 1L;

    private Long size = 10L;

    private String gid;
}
