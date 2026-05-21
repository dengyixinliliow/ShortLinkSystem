package com.example.project.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_link_goto")
public class LinkGotoDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String fullShortUrl;

    private String gid;
}
