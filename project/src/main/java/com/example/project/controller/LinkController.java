package com.example.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.project.common.convention.result.Result;
import com.example.project.common.convention.result.Results;
import com.example.project.dto.req.ShortLinkCreateReqDTO;
import com.example.project.dto.req.ShortLinkGroupCountReqDTO;
import com.example.project.dto.req.ShortLinkPageReqDTO;
import com.example.project.dto.req.ShortLinkUpdateReqDTO;
import com.example.project.dto.resp.ShortLinkCreateRespDTO;
import com.example.project.dto.resp.ShortLinkPageRespDTO;
import com.example.project.service.LinkService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;

    @PostMapping("/api/shortlink/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return Results.success(linkService.createShortLink(requestParam));
    }

    @PutMapping("/api/shortlink/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam) {
        linkService.updateShortLink(requestParam);
        return Results.success();
    }

    @GetMapping("/api/shortlink/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        return Results.success(linkService.pageShortLink(requestParam));
    }

    @PostMapping("/api/shortlink/v1/count")
    public Result<Map<String, Long>> countShortLinkByGroup(@RequestBody ShortLinkGroupCountReqDTO requestParam) {
        return Results.success(linkService.countShortLinkByGroup(requestParam));
    }

    @GetMapping("/{shortUri}")
    public void restoreUrl(@PathVariable("shortUri") String shortUri,
                           HttpServletRequest request,
                           HttpServletResponse response) throws IOException {
        String originUrl = linkService.restoreUrl(request.getRequestURL().toString());
        response.sendRedirect(originUrl);
    }
}
