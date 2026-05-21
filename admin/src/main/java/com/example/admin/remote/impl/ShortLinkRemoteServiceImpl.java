package com.example.admin.remote.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.admin.common.convention.result.Result;
import com.example.admin.remote.ShortLinkRemoteService;
import com.example.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.example.admin.remote.dto.req.ShortLinkGroupCountReqDTO;
import com.example.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.example.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import com.example.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.example.admin.remote.dto.resp.ShortLinkPageRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShortLinkRemoteServiceImpl implements ShortLinkRemoteService {

    private final RestTemplate restTemplate;

    @Value("${short-link.project.base-url:http://localhost:8002}")
    private String projectBaseUrl;

    @Override
    public Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO requestParam) {
        ResponseEntity<Result<ShortLinkCreateRespDTO>> response = restTemplate.exchange(
                projectBaseUrl + "/api/shortlink/v1/create",
                HttpMethod.POST,
                new HttpEntity<>(requestParam),
                new ParameterizedTypeReference<Result<ShortLinkCreateRespDTO>>() {
                });
        return response.getBody();
    }

    @Override
    public Result<Void> updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        ResponseEntity<Result<Void>> response = restTemplate.exchange(
                projectBaseUrl + "/api/shortlink/v1/update",
                HttpMethod.PUT,
                new HttpEntity<>(requestParam),
                new ParameterizedTypeReference<Result<Void>>() {
                });
        return response.getBody();
    }

    @Override
    public Result<Page<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        String url = UriComponentsBuilder.fromHttpUrl(projectBaseUrl + "/api/shortlink/v1/page")
                .queryParam("current", requestParam.getCurrent())
                .queryParam("size", requestParam.getSize())
                .queryParam("gid", requestParam.getGid())
                .toUriString();
        ResponseEntity<Result<Page<ShortLinkPageRespDTO>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Result<Page<ShortLinkPageRespDTO>>>() {
        });
        return response.getBody();
    }

    @Override
    public Result<Map<String, Long>> countShortLinkByGroup(ShortLinkGroupCountReqDTO requestParam) {
        ResponseEntity<Result<Map<String, Long>>> response = restTemplate.exchange(
                projectBaseUrl + "/api/shortlink/v1/count",
                HttpMethod.POST,
                new HttpEntity<>(requestParam),
                new ParameterizedTypeReference<Result<Map<String, Long>>>() {
                });
        return response.getBody();
    }
}
