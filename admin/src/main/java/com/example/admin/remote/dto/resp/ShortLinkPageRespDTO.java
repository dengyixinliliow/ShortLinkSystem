package com.example.admin.remote.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShortLinkPageRespDTO {

    private Long id;

    private String domain;

    private String shortUri;

    private String fullShortUrl;

    private String originUrl;

    private String favicon;

    private Integer clickNum;

    private String gid;

    private Integer validDateType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validDate;

    private String describe;
}
