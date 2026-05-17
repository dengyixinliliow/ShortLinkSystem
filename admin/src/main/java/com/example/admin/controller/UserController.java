package com.example.admin.controller;

import com.example.admin.common.convention.exception.ClientException;
import com.example.admin.common.convention.result.Result;
import com.example.admin.common.convention.result.Results;
import com.example.admin.common.enums.UserErrorCodeEnum;
import com.example.admin.dto.req.UserLoginReqDTO;
import com.example.admin.dto.req.UserRegisterReqDTO;
import com.example.admin.dto.req.UserUpdateReqDTO;
import com.example.admin.dto.resp.UserLoginRespDTO;
import com.example.admin.dto.resp.UserRespDTO;
import com.example.admin.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/shortlink/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable String username) {
        UserRespDTO result = userService.getUserByUsername(username);
        if (result == null) {
            throw new ClientException(UserErrorCodeEnum.USER_NULL);
        }
        return Results.success(result);
    }

    @GetMapping("/api/shortlink/v1/user/has-username/{username}")
    public Result<Boolean> hasUsername(@PathVariable("username") String username) {
        return Results.success(userService.hasUsername(username));
    }

    @PostMapping("/api/shortlink/v1/user")
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }

    @PutMapping("/api/shortlink/v1/user")
    public Result<Void> update(@RequestBody UserUpdateReqDTO requestParam) {
        userService.update(requestParam);
        return Results.success();
    }

    @PostMapping("/api/shortlink/v1/user/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestParam) {
        return Results.success(userService.login(requestParam));
    }

    @GetMapping("/api/shortlink/v1/user/check-login/{username}")
    public Result<Boolean> checkLogin(@PathVariable("username") String username) {
        return Results.success(userService.checkLogin(username));
    }

    @DeleteMapping("/api/shortlink/v1/user/logout/{username}")
    public Result<Void> logout(@PathVariable("username") String username) {
        userService.logout(username);
        return Results.success();
    }
}
