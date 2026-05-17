package com.example.project.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.project.common.database.BaseDO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_link")
public class LinkDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String domain;

    private String shortUri;

    private String fullShortUrl;

    private String originUrl;

    private Integer clickNum;

    private String gid;

    private Integer enableStatus;

    private Integer createdType;

    private Integer validDateType;

    private LocalDateTime validDate;

    private String describe;
}
