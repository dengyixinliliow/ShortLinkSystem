package com.example.project.dto.resp;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

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

    @TableField("`describe`")
    private String describe;
}
