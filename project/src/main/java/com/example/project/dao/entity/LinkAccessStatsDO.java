package com.example.project.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.project.common.database.BaseDO;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("t_link_access_stats")
public class LinkAccessStatsDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String fullShortUrl;

    private String gid;

    private LocalDate date;

    private Integer pv;

    private Integer uv;

    private Integer uip;

    private Integer hour;

    private Integer weekday;
}
