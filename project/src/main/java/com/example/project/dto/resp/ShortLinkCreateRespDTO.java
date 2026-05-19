package com.example.project.dto.resp;

import lombok.Data;

@Data
public class ShortLinkCreateRespDTO {

    private String gid;

    private String originUrl;

    private String fullShortUrl;
}
