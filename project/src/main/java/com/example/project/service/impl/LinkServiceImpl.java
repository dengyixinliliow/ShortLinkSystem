package com.example.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.project.common.constants.RedisKeyConstant;
import com.example.project.common.convention.exception.ServiceException;
import com.example.project.common.enums.ValidDateTypeEnum;
import com.example.project.dao.entity.LinkDO;
import com.example.project.dao.entity.LinkGotoDO;
import com.example.project.dao.mapper.LinkAccessStatsMapper;
import com.example.project.dao.mapper.LinkGotoMapper;
import com.example.project.dao.mapper.LinkMapper;
import com.example.project.dto.req.ShortLinkCreateReqDTO;
import com.example.project.dto.req.ShortLinkGroupCountReqDTO;
import com.example.project.dto.req.ShortLinkPageReqDTO;
import com.example.project.dto.req.ShortLinkUpdateReqDTO;
import com.example.project.dto.resp.ShortLinkCreateRespDTO;
import com.example.project.dto.resp.ShortLinkPageRespDTO;
import com.example.project.service.LinkService;
import com.example.project.util.HashUtil;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LinkServiceImpl extends ServiceImpl<LinkMapper, LinkDO> implements LinkService {

    private static final int MAX_GENERATE_COUNT = 10;

    private static final String SHORT_LINK_UV_COOKIE_NAME = "short_link_uv";

    private static final String UNKNOWN_IP = "unknown";

    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;

    private final LinkGotoMapper linkGotoMapper;

    private final LinkAccessStatsMapper linkAccessStatsMapper;

    private final StringRedisTemplate stringRedisTemplate;

    private final RedissonClient redissonClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        validateCreateRequest(requestParam);
        String shortLinkSuffix = generateSuffix(requestParam);
        LinkDO linkDO = BeanUtil.toBean(requestParam, LinkDO.class);
        linkDO.setShortUri(shortLinkSuffix);
        linkDO.setFullShortUrl(requestParam.getDomain() + "/" + shortLinkSuffix);
        linkDO.setEnableStatus(0);
        baseMapper.insert(linkDO);
        LinkGotoDO linkGotoDO = new LinkGotoDO();
        linkGotoDO.setFullShortUrl(linkDO.getFullShortUrl());
        linkGotoDO.setGid(linkDO.getGid());
        linkGotoMapper.insert(linkGotoDO);
        shortUriCreateCachePenetrationBloomFilter.add(linkDO.getFullShortUrl());
        setShortLinkGotoCache(linkDO);

        ShortLinkCreateRespDTO result = new ShortLinkCreateRespDTO();
        result.setFullShortUrl(linkDO.getFullShortUrl());
        result.setOriginUrl(requestParam.getOriginUrl());
        result.setGid(requestParam.getGid());
        return result;
    }

    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        validateUpdateRequest(requestParam);
        // TODO: Support updating gid and fullShortUrl after handling shard migration, unique checks, and BloomFilter consistency.
        LambdaUpdateWrapper<LinkDO> updateWrapper = Wrappers.lambdaUpdate(LinkDO.class)
                .eq(LinkDO::getGid, requestParam.getGid())
                .eq(LinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(LinkDO::getDelFlag, 0)
                .set(StrUtil.isNotBlank(requestParam.getOriginUrl()), LinkDO::getOriginUrl, requestParam.getOriginUrl())
                .set(StrUtil.isNotBlank(requestParam.getFavicon()), LinkDO::getFavicon, requestParam.getFavicon())
                .set(requestParam.getEnableStatus() != null, LinkDO::getEnableStatus, requestParam.getEnableStatus())
                .set(StrUtil.isNotBlank(requestParam.getDescribe()), LinkDO::getDescribe, requestParam.getDescribe());
        if (requestParam.getValidDateType() != null) {
            updateWrapper.set(LinkDO::getValidDateType, requestParam.getValidDateType())
                    .set(LinkDO::getValidDate, requestParam.getValidDate());
        }
        boolean updateResult = update(updateWrapper);
        if (!updateResult) {
            throw new ServiceException("Short link not found");
        }
    }

    private void validateCreateRequest(ShortLinkCreateReqDTO requestParam) {
        if (!ValidDateTypeEnum.contains(requestParam.getValidDateType())) {
            throw new ServiceException("Invalid valid date type");
        }
        if (ValidDateTypeEnum.isCustom(requestParam.getValidDateType()) && requestParam.getValidDate() == null) {
            throw new ServiceException("Valid date cannot be null");
        }
        if (ValidDateTypeEnum.isCustom(requestParam.getValidDateType())
                && !requestParam.getValidDate().isAfter(LocalDateTime.now())) {
            throw new ServiceException("Valid date must be later than current time");
        }
    }

    private void validateUpdateRequest(ShortLinkUpdateReqDTO requestParam) {
        if (requestParam == null || StrUtil.isBlank(requestParam.getGid())
                || StrUtil.isBlank(requestParam.getFullShortUrl())) {
            throw new ServiceException("Short link update key cannot be null");
        }
        if (requestParam.getValidDateType() == null) {
            return;
        }
        if (!ValidDateTypeEnum.contains(requestParam.getValidDateType())) {
            throw new ServiceException("Invalid valid date type");
        }
        if (ValidDateTypeEnum.isCustom(requestParam.getValidDateType()) && requestParam.getValidDate() == null) {
            throw new ServiceException("Valid date cannot be null");
        }
    }

    @Override
    public String restoreUrl(String fullShortUrl, HttpServletRequest request, HttpServletResponse response) {
        if (StrUtil.isBlank(fullShortUrl)) {
            throw new ServiceException("Short link cannot be null");
        }
        String cacheKey = String.format(RedisKeyConstant.GOTO_SHORT_LINK_KEY, fullShortUrl);
        String cachedOriginUrl = stringRedisTemplate.opsForValue().get(cacheKey);
        if (StrUtil.isNotBlank(cachedOriginUrl)) {
            LinkGotoDO linkGotoDO = getLinkGoto(fullShortUrl);
            if (linkGotoDO != null) {
                recordAccessStats(fullShortUrl, linkGotoDO.getGid(), request, response);
            }
            return cachedOriginUrl;
        }
        if (!shortUriCreateCachePenetrationBloomFilter.contains(fullShortUrl)) {
            throw new ServiceException("Short link not found");
        }
        RLock lock = redissonClient.getLock(String.format(RedisKeyConstant.LOCK_GOTO_SHORT_LINK_KEY, fullShortUrl));
        lock.lock();
        try {
            cachedOriginUrl = stringRedisTemplate.opsForValue().get(cacheKey);
            if (StrUtil.isNotBlank(cachedOriginUrl)) {
                LinkGotoDO linkGotoDO = getLinkGoto(fullShortUrl);
                if (linkGotoDO != null) {
                    recordAccessStats(fullShortUrl, linkGotoDO.getGid(), request, response);
                }
                return cachedOriginUrl;
            }
            LinkGotoDO linkGotoDO = getLinkGoto(fullShortUrl);
            if (linkGotoDO == null) {
                throw new ServiceException("Short link not found");
            }
            LinkDO linkDO = baseMapper.selectOne(Wrappers.lambdaQuery(LinkDO.class)
                    .eq(LinkDO::getGid, linkGotoDO.getGid())
                    .eq(LinkDO::getFullShortUrl, fullShortUrl)
                    .eq(LinkDO::getDelFlag, 0));
            if (linkDO == null) {
                throw new ServiceException("Short link not found");
            }
            if (ValidDateTypeEnum.isCustom(linkDO.getValidDateType())
                    && linkDO.getValidDate() != null
                    && linkDO.getValidDate().isBefore(LocalDateTime.now())) {
                throw new ServiceException("Short link not found");
            }
            setShortLinkGotoCache(linkDO);
            recordAccessStats(fullShortUrl, linkDO.getGid(), request, response);
            return linkDO.getOriginUrl();
        } finally {
            lock.unlock();
        }
    }

    private LinkGotoDO getLinkGoto(String fullShortUrl) {
        return linkGotoMapper.selectOne(Wrappers.lambdaQuery(LinkGotoDO.class)
                .eq(LinkGotoDO::getFullShortUrl, fullShortUrl));
    }

    private void recordAccessStats(String fullShortUrl, String gid, HttpServletRequest request, HttpServletResponse response) {
        LocalDateTime now = LocalDateTime.now();
        Integer uvIncrement = markUvAndGetIncrement(fullShortUrl, now, request, response);
        Integer uipIncrement = markUipAndGetIncrement(fullShortUrl, now, request);
        linkAccessStatsMapper.incrementStats(
                fullShortUrl,
                gid,
                LocalDate.from(now),
                now.getHour(),
                now.getDayOfWeek().getValue(),
                uvIncrement,
                uipIncrement);
    }

    private Integer markUvAndGetIncrement(String fullShortUrl,
                                          LocalDateTime now,
                                          HttpServletRequest request,
                                          HttpServletResponse response) {
        String visitorId = getOrCreateVisitorId(request, response);
        String uvKey = String.format(
                RedisKeyConstant.SHORT_LINK_STATS_UV_KEY,
                fullShortUrl,
                LocalDate.from(now),
                now.getHour());
        Boolean firstVisit = stringRedisTemplate.opsForSet().add(uvKey, visitorId) == 1;
        stringRedisTemplate.expire(uvKey, RedisKeyConstant.SHORT_LINK_STATS_UV_TTL);
        return Boolean.TRUE.equals(firstVisit) ? 1 : 0;
    }

    private Integer markUipAndGetIncrement(String fullShortUrl, LocalDateTime now, HttpServletRequest request) {
        String clientIp = getClientIp(request);
        String uipKey = String.format(
                RedisKeyConstant.SHORT_LINK_STATS_UIP_KEY,
                fullShortUrl,
                LocalDate.from(now),
                now.getHour());
        Boolean firstVisit = stringRedisTemplate.opsForSet().add(uipKey, clientIp) == 1;
        stringRedisTemplate.expire(uipKey, RedisKeyConstant.SHORT_LINK_STATS_UIP_TTL);
        return Boolean.TRUE.equals(firstVisit) ? 1 : 0;
    }

    private String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (StrUtil.isNotBlank(forwardedFor) && !UNKNOWN_IP.equalsIgnoreCase(forwardedFor)) {
            return forwardedFor.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (StrUtil.isNotBlank(realIp) && !UNKNOWN_IP.equalsIgnoreCase(realIp)) {
            return realIp;
        }
        return request.getRemoteAddr();
    }

    private String getOrCreateVisitorId(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<Cookie> visitorCookie = Arrays.stream(cookies)
                    .filter(each -> SHORT_LINK_UV_COOKIE_NAME.equals(each.getName()))
                    .findFirst();
            if (visitorCookie.isPresent() && StrUtil.isNotBlank(visitorCookie.get().getValue())) {
                return visitorCookie.get().getValue();
            }
        }
        String visitorId = UUID.randomUUID().toString();
        Cookie cookie = new Cookie(SHORT_LINK_UV_COOKIE_NAME, visitorId);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) Duration.ofDays(30).getSeconds());
        response.addCookie(cookie);
        return visitorId;
    }

    private void setShortLinkGotoCache(LinkDO linkDO) {
        stringRedisTemplate.opsForValue().set(
                String.format(RedisKeyConstant.GOTO_SHORT_LINK_KEY, linkDO.getFullShortUrl()),
                linkDO.getOriginUrl(),
                getShortLinkGotoCacheTtl(linkDO));
    }

    private Duration getShortLinkGotoCacheTtl(LinkDO linkDO) {
        if (ValidDateTypeEnum.isCustom(linkDO.getValidDateType())) {
            Duration ttl = Duration.between(LocalDateTime.now(), linkDO.getValidDate());
            if (ttl.isNegative() || ttl.isZero()) {
                throw new ServiceException("Short link not found");
            }
            return ttl;
        }
        return RedisKeyConstant.GOTO_SHORT_LINK_PERMANENT_TTL;
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
