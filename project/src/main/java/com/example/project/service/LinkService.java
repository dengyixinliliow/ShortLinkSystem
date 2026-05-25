package com.example.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.project.dao.entity.LinkDO;
import com.example.project.dto.req.ShortLinkCreateReqDTO;
import com.example.project.dto.req.ShortLinkGroupCountReqDTO;
import com.example.project.dto.req.ShortLinkPageReqDTO;
import com.example.project.dto.req.ShortLinkUpdateReqDTO;
import com.example.project.dto.resp.ShortLinkCreateRespDTO;
import com.example.project.dto.resp.ShortLinkPageRespDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface LinkService extends IService<LinkDO> {

    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);

    void updateShortLink(ShortLinkUpdateReqDTO requestParam);

    String restoreUrl(String fullShortUrl, HttpServletRequest request, HttpServletResponse response);

    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam);

    Map<String, Long> countShortLinkByGroup(ShortLinkGroupCountReqDTO requestParam);
}
