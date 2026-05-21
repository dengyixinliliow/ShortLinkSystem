package com.example.admin.remote.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validDate;

    private String describe;
}
