package com.example.project.service.impl;

import cn.hutool.core.bean.BeanUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.project.common.convention.exception.ServiceException;
import com.example.project.dao.entity.LinkDO;
import com.example.project.dao.mapper.LinkMapper;
import com.example.project.dto.req.ShortLinkCreateReqDTO;
import com.example.project.dto.req.ShortLinkGroupCountReqDTO;
import com.example.project.dto.req.ShortLinkPageReqDTO;
import com.example.project.dto.resp.ShortLinkCreateRespDTO;
import com.example.project.dto.resp.ShortLinkPageRespDTO;
import com.example.project.service.LinkService;
import com.example.project.util.HashUtil;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LinkServiceImpl extends ServiceImpl<LinkMapper, LinkDO> implements LinkService {

    private static final int MAX_GENERATE_COUNT = 10;

    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String shortLinkSuffix = generateSuffix(requestParam);
        LinkDO linkDO = BeanUtil.toBean(requestParam, LinkDO.class);
        linkDO.setShortUri(shortLinkSuffix);
        linkDO.setFullShortUrl(requestParam.getDomain() + "/" + shortLinkSuffix);
        linkDO.setEnableStatus(0);
        baseMapper.insert(linkDO);
        shortUriCreateCachePenetrationBloomFilter.add(linkDO.getFullShortUrl());

        ShortLinkCreateRespDTO result = new ShortLinkCreateRespDTO();
        result.setFullShortUrl(linkDO.getFullShortUrl());
        result.setOriginUrl(requestParam.getOriginUrl());
        result.setGid(requestParam.getGid());
        return result;
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        LambdaQueryWrapper<LinkDO> queryWrapper = Wrappers.lambdaQuery(LinkDO.class)
                .eq(LinkDO::getGid, requestParam.getGid())
                .eq(LinkDO::getDelFlag, 0)
                .orderByDesc(LinkDO::getCreateTime);
        IPage<LinkDO> linkPage = baseMapper.selectPage(
                new Page<>(requestParam.getCurrent(), requestParam.getSize()),
                queryWrapper);
        return linkPage.convert(each -> BeanUtil.toBean(each, ShortLinkPageRespDTO.class));
    }

    @Override
    public Map<String, Long> countShortLinkByGroup(ShortLinkGroupCountReqDTO requestParam) {
        return Optional.ofNullable(requestParam.getGidList())
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(
                        gid -> gid,
                        gid -> baseMapper.selectCount(Wrappers.lambdaQuery(LinkDO.class)
                                .eq(LinkDO::getGid, gid)
                                .eq(LinkDO::getDelFlag, 0))));
    }

    private String generateSuffix(ShortLinkCreateReqDTO requestParam) {
        int customGenerateCount = 0;
        String shortUri;
        while (true) {
            if (customGenerateCount > MAX_GENERATE_COUNT) {
                throw new ServiceException("Short link generation failed, please try again later");
            }
            String originUrl = requestParam.getOriginUrl();
            shortUri = HashUtil.hashToBase62(originUrl + customGenerateCount);
            String fullShortUrl = requestParam.getDomain() + "/" + shortUri;
            if (!shortUriCreateCachePenetrationBloomFilter.contains(fullShortUrl)) {
                break;
            }
            LambdaQueryWrapper<LinkDO> queryWrapper = Wrappers.lambdaQuery(LinkDO.class)
                    .eq(LinkDO::getFullShortUrl, fullShortUrl);
            LinkDO linkDO = baseMapper.selectOne(queryWrapper);
            if (linkDO == null) {
                break;
            }
            customGenerateCount++;
        }

        return shortUri;
    }
}
