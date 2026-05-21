package com.example.admin.remote;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.admin.common.convention.result.Result;
import com.example.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.example.admin.remote.dto.req.ShortLinkGroupCountReqDTO;
import com.example.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.example.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import com.example.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.example.admin.remote.dto.resp.ShortLinkPageRespDTO;

import java.util.Map;

public interface ShortLinkRemoteService {

    Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO requestParam);

    Result<Void> updateShortLink(ShortLinkUpdateReqDTO requestParam);

    Result<Page<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam);

    Result<Map<String, Long>> countShortLinkByGroup(ShortLinkGroupCountReqDTO requestParam);
}
