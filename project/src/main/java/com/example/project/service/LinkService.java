package com.example.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.project.dao.entity.LinkDO;
import com.example.project.dto.req.ShortLinkCreateReqDTO;
import com.example.project.dto.resp.ShortLinkCreateRespDTO;

public interface LinkService extends IService<LinkDO> {

    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);
}
