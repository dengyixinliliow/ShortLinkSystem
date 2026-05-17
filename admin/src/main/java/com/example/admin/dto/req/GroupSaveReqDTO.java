package com.example.admin.dto.req;

import lombok.Data;

@Data
public class GroupSaveReqDTO {

    private String gid;

    private String name;

    private String username;

    private Integer sortOrder;
}
