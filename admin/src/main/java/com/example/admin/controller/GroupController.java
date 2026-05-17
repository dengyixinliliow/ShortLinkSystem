package com.example.admin.controller;

import com.example.admin.common.convention.result.Result;
import com.example.admin.common.convention.result.Results;
import com.example.admin.dao.entity.GroupDO;
import com.example.admin.dto.req.GroupSaveReqDTO;
import com.example.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping("/api/shortlink/v1/group")
    public Result<Void> save(@RequestBody GroupSaveReqDTO requestParam) {
        GroupDO groupDO = new GroupDO();
        BeanUtils.copyProperties(requestParam, groupDO);
        groupService.save(groupDO);
        return Results.success();
    }
}
