package com.example.admin.remote.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShortLinkUpdateReqDTO {

    private String gid;

    private String fullShortUrl;

    private String originUrl;

    private String favicon;

    private Integer validDateType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validDate;

    private String describe;

    private Integer enableStatus;
}
