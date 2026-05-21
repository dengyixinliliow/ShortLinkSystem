package com.example.admin.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.admin.common.constants.RedisCacheConstants;
import com.example.admin.common.convention.exception.ClientException;
import com.example.admin.common.convention.exception.ServiceException;
import com.example.admin.common.enums.UserErrorCodeEnum;
import com.example.admin.dao.entity.UserDO;
import com.example.admin.dto.req.UserLoginReqDTO;
import com.example.admin.dto.req.UserRegisterReqDTO;
import com.example.admin.dto.req.UserUpdateReqDTO;
import com.example.admin.dto.resp.UserLoginRespDTO;
import com.example.admin.dto.resp.UserRespDTO;
import com.example.admin.mapper.UserMapper;
import com.example.admin.service.GroupService;
import com.example.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;

    private final RedissonClient redissonClient;

    private final StringRedisTemplate stringRedisTemplate;

    private final GroupService groupService;

    @Override
    public UserRespDTO getUserByUsername(String username) {
        UserDO userDO = baseMapper.selectOne(new LambdaQueryWrapper<UserDO>()
                .eq(UserDO::getUsername, username));
        if (userDO == null) {
            return null;
        }
        UserRespDTO result = new UserRespDTO();
        BeanUtils.copyProperties(userDO, result);
        return result;
    }

    @Override
    public Boolean hasUsername(String username) {
        if (!userRegisterCachePenetrationBloomFilter.contains(username)) {
            return Boolean.FALSE;
        }
        return baseMapper.exists(new LambdaQueryWrapper<UserDO>()
                .eq(UserDO::getUsername, username));
    }

    @Override
    public void register(UserRegisterReqDTO requestParam) {
        String username = requestParam.getUsername();
        if (hasUsername(username)) {
            throw new ClientException(UserErrorCodeEnum.USER_NAME_EXIST);
        }
        // Prevent malicious concurrent requests from registering the same nonexistent username.
        RLock lock = redissonClient.getLock(RedisCacheConstants.LOCK_USER_REGISTER_KEY + username);
        if (!lock.tryLock()) {
            throw new ClientException(UserErrorCodeEnum.USER_NAME_EXIST);
        }
        try {
            if (hasUsername(username)) {
                throw new ClientException(UserErrorCodeEnum.USER_NAME_EXIST);
            }
            UserDO userDO = new UserDO();
            BeanUtils.copyProperties(requestParam, userDO);
            boolean saveResult = save(userDO);
            if (!saveResult) {
                throw new ServiceException(UserErrorCodeEnum.USER_SAVE_ERROR);
            }
            groupService.saveGroup(username, "Default");
            userRegisterCachePenetrationBloomFilter.add(username);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void update(UserUpdateReqDTO requestParam) {
        String username = requestParam.getUsername();
        if (!hasUsername(username)) {
            throw new ClientException(UserErrorCodeEnum.USER_NULL);
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(requestParam, userDO);
        boolean updateResult = update(userDO, new LambdaUpdateWrapper<UserDO>()
                .eq(UserDO::getUsername, username));
        if (!updateResult) {
            throw new ServiceException(UserErrorCodeEnum.USER_UPDATE_ERROR);
        }
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        UserDO userDO = baseMapper.selectOne(new LambdaQueryWrapper<UserDO>()
                .eq(UserDO::getUsername, requestParam.getUsername())
                .eq(UserDO::getPassword, requestParam.getPassword())
                .eq(UserDO::getDelFlag, 0));
        if (userDO == null) {
            throw new ClientException(UserErrorCodeEnum.USER_LOGIN_ERROR);
        }
        String loginKey = RedisCacheConstants.USER_LOGIN_KEY + requestParam.getUsername();
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(loginKey))) {
            throw new ClientException(UserErrorCodeEnum.USER_ALREADY_LOGIN);
        }
        String token = IdUtil.fastSimpleUUID();
        UserLoginRespDTO result = new UserLoginRespDTO();
        result.setToken(token);
        stringRedisTemplate.opsForHash().put(loginKey, token, JSONUtil.toJsonStr(userDO));
        stringRedisTemplate.expire(loginKey, RedisCacheConstants.USER_LOGIN_TOKEN_TTL);
        return result;
    }

    @Override
    public Boolean checkLogin(String username) {
        if (StrUtil.isBlank(username)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(RedisCacheConstants.USER_LOGIN_KEY + username));
    }

    @Override
    public void logout(String username) {
        if (!checkLogin(username)) {
            throw new ClientException(UserErrorCodeEnum.USER_NOT_LOGIN);
        }
        stringRedisTemplate.delete(RedisCacheConstants.USER_LOGIN_KEY + username);
    }
}
