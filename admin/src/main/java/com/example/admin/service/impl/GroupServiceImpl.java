package com.example.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.admin.common.biz.user.UserContext;
import com.example.admin.common.convention.exception.ClientException;
import com.example.admin.common.enums.GroupErrorCodeEnum;
import com.example.admin.common.enums.UserErrorCodeEnum;
import com.example.admin.dao.entity.GroupDO;
import com.example.admin.dto.req.GroupSaveReqDTO;
import com.example.admin.dto.req.GroupUpdateReqDTO;
import com.example.admin.dto.resp.GroupRespDTO;
import com.example.admin.mapper.GroupMapper;
import com.example.admin.remote.ShortLinkRemoteService;
import com.example.admin.remote.dto.req.ShortLinkGroupCountReqDTO;
import com.example.admin.service.GroupService;
import com.example.admin.toolkit.RandomGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    private final ShortLinkRemoteService shortLinkRemoteService;

    @Override
    public void saveGroup(GroupSaveReqDTO requestParam) {
        if (requestParam == null || StrUtil.isBlank(requestParam.getGroupName())) {
            throw new ClientException(GroupErrorCodeEnum.GROUP_NAME_NULL);
        }
        String username = getCurrentUsername();
        String gid = generateUniqueGid();
        GroupDO groupDO = new GroupDO();
        groupDO.setGid(gid);
        groupDO.setName(requestParam.getGroupName());
        groupDO.setUsername(username);
        groupDO.setSortOrder(0);
        save(groupDO);
    }

    @Override
    public void updateGroup(GroupUpdateReqDTO requestParam) {
        if (requestParam == null || StrUtil.isBlank(requestParam.getGid())) {
            throw new ClientException(GroupErrorCodeEnum.GROUP_GID_NULL);
        }
        if (StrUtil.isBlank(requestParam.getGroupName())) {
            throw new ClientException(GroupErrorCodeEnum.GROUP_NAME_NULL);
        }
        String username = getCurrentUsername();
        boolean updateResult = update(new LambdaUpdateWrapper<GroupDO>()
                .eq(GroupDO::getGid, requestParam.getGid())
                .eq(GroupDO::getUsername, username)
                .eq(GroupDO::getDelFlag, 0)
                .set(GroupDO::getName, requestParam.getGroupName()));
        if (!updateResult) {
            throw new ClientException(GroupErrorCodeEnum.GROUP_NOT_FOUND);
        }
    }

    @Override
    public void deleteGroup(String gid) {
        if (StrUtil.isBlank(gid)) {
            throw new ClientException(GroupErrorCodeEnum.GROUP_GID_NULL);
        }
        String username = getCurrentUsername();
        boolean deleteResult = update(new LambdaUpdateWrapper<GroupDO>()
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, username)
                .eq(GroupDO::getDelFlag, 0)
                .set(GroupDO::getDelFlag, 1));
        if (!deleteResult) {
            throw new ClientException(GroupErrorCodeEnum.GROUP_NOT_FOUND);
        }
    }

    @Override
    public List<GroupRespDTO> listGroup() {
        String username = getCurrentUsername();
        List<GroupDO> groupDOList = baseMapper.selectList(new LambdaQueryWrapper<GroupDO>()
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getUsername, username)
                .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime));
        ShortLinkGroupCountReqDTO countReqDTO = new ShortLinkGroupCountReqDTO();
        countReqDTO.setGidList(groupDOList.stream().map(GroupDO::getGid).toList());
        Map<String, Long> shortLinkCountMap = Optional.ofNullable(shortLinkRemoteService.countShortLinkByGroup(countReqDTO))
                .map(each -> each.getData())
                .orElse(Collections.emptyMap());
        return groupDOList.stream()
                .map(each -> {
                    GroupRespDTO result = new GroupRespDTO();
                    result.setGid(each.getGid());
                    result.setGroupName(each.getName());
                    result.setShortLinkCount(Optional.ofNullable(shortLinkCountMap.get(each.getGid()))
                            .map(Long::intValue)
                            .orElse(0));
                    result.setSortOrder(each.getSortOrder());
                    return result;
                })
                .toList();
    }

    private String generateUniqueGid() {
        String gid;
        do {
            gid = RandomGenerator.generateRandomGid();
        } while (baseMapper.exists(new LambdaQueryWrapper<GroupDO>()
                .eq(GroupDO::getGid, gid)));
        return gid;
    }

    private String getCurrentUsername() {
        String username = UserContext.getUsername();
        if (StrUtil.isBlank(username)) {
            throw new ClientException(UserErrorCodeEnum.USER_NOT_LOGIN);
        }
        return username;
    }
}
