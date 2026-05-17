package com.example.admin.dao.entity;

import com.example.admin.common.database.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_group")
public class GroupDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String gid;

    private String name;

    private String username;

    private Integer sortOrder;
}
