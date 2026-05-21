package com.example.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.admin.common.convention.result.Result;
import com.example.admin.remote.ShortLinkRemoteService;
import com.example.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.example.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.example.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.example.admin.remote.dto.resp.ShortLinkPageRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ShortLinkController {

    private final ShortLinkRemoteService shortLinkRemoteService;

    @PostMapping("/api/shortlink/admin/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return shortLinkRemoteService.createShortLink(requestParam);
    }

    @GetMapping("/api/shortlink/admin/v1/page")
    public Result<Page<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        return shortLinkRemoteService.pageShortLink(requestParam);
    }
}
