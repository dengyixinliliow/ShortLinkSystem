package com.example.project.controller;

import com.example.project.common.convention.result.Result;
import com.example.project.common.convention.result.Results;
import com.example.project.dto.req.ShortLinkCreateReqDTO;
import com.example.project.dto.resp.ShortLinkCreateRespDTO;
import com.example.project.service.LinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;

    @PostMapping("/api/shortlink/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return Results.success(linkService.createShortLink(requestParam));
    }
}
