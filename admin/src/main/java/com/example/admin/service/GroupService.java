package com.example.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.admin.dao.entity.GroupDO;
import com.example.admin.dto.req.GroupSaveReqDTO;
import com.example.admin.dto.req.GroupUpdateReqDTO;
import com.example.admin.dto.resp.GroupRespDTO;

import java.util.List;

public interface GroupService extends IService<GroupDO> {

    void saveGroup(GroupSaveReqDTO requestParam);

    void saveGroup(String groupName);

    void saveGroup(String username, String groupName);

    void updateGroup(GroupUpdateReqDTO requestParam);

    void deleteGroup(String gid);

    List<GroupRespDTO> listGroup();
}
