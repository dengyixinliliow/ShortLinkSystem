package com.example.admin.remote;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.admin.common.convention.result.Result;
import com.example.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.example.admin.remote.dto.req.ShortLinkGroupCountReqDTO;
import com.example.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.example.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import com.example.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.example.admin.remote.dto.resp.ShortLinkPageRespDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "shortlink-project", configuration = ShortLinkRemoteServiceConfiguration.class)
public interface ShortLinkRemoteService {

    @PostMapping("/api/shortlink/v1/create")
    Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam);

    @PutMapping("/api/shortlink/v1/update")
    Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam);

    @GetMapping("/api/shortlink/v1/page")
    Result<Page<ShortLinkPageRespDTO>> pageShortLink(@RequestParam("current") Long current,
                                                     @RequestParam("size") Long size,
                                                     @RequestParam("gid") String gid);

    @PostMapping("/api/shortlink/v1/count")
    Result<Map<String, Long>> countShortLinkByGroup(@RequestBody ShortLinkGroupCountReqDTO requestParam);

    @GetMapping("/api/shortlink/v1/title")
    Result<String> getTitleByUrl(@RequestParam("fullUrl") String fullUrl);
}
