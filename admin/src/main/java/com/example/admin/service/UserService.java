package com.example.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.admin.dao.entity.UserDO;
import com.example.admin.dto.req.UserLoginReqDTO;
import com.example.admin.dto.req.UserRegisterReqDTO;
import com.example.admin.dto.req.UserUpdateReqDTO;
import com.example.admin.dto.resp.UserLoginRespDTO;
import com.example.admin.dto.resp.UserRespDTO;

public interface UserService extends IService<UserDO> {

    UserRespDTO getUserByUsername(String username);

    Boolean hasUsername(String username);

    void register(UserRegisterReqDTO requestParam);

    void update(UserUpdateReqDTO requestParam);

    UserLoginRespDTO login(UserLoginReqDTO requestParam);

    Boolean checkLogin(String username);

    void logout(String username);
}
