package com.example.admin.controller;

import com.example.admin.common.convention.result.Result;
import com.example.admin.common.convention.result.Results;
import com.example.admin.dto.req.GroupSaveReqDTO;
import com.example.admin.dto.req.GroupUpdateReqDTO;
import com.example.admin.dto.resp.GroupRespDTO;
import com.example.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping("/api/shortlink/v1/group")
    public Result<Void> save(@RequestBody GroupSaveReqDTO requestParam) {
        groupService.saveGroup(requestParam);
        return Results.success();
    }

    @PutMapping("/api/shortlink/v1/group")
    public Result<Void> update(@RequestBody GroupUpdateReqDTO requestParam) {
        groupService.updateGroup(requestParam);
        return Results.success();
    }

    @DeleteMapping("/api/shortlink/v1/group/{gid}")
    public Result<Void> delete(@PathVariable String gid) {
        groupService.deleteGroup(gid);
        return Results.success();
    }

    @GetMapping("/api/shortlink/v1/group")
    public Result<List<GroupRespDTO>> listGroup() {
        return Results.success(groupService.listGroup());
    }
}
