package com.blog.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.blog.common.Result;
import com.blog.dto.LoginDto;
import com.blog.dto.PasswordDto;
import com.blog.entity.User;
import com.blog.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证接口：登录、获取当前用户信息、修改密码和个人资料。
 * <p>
 * 登录成功后通过 {@code StpUtil.login(userId)} 写入 Sa-Token 会话，
 * 后续请求由 {@code SaTokenConfig} 中的拦截器自动校验。
 * 密码使用 BCrypt 加密，注册流程未开放（管理员由 DataInitializer 初始化）。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDto dto) {
        User user = userService.login(dto.getUsername(), dto.getPassword());
        StpUtil.login(user.getId());
        Map<String, Object> map = new HashMap<>();
        map.put("token", StpUtil.getTokenValue());
        map.put("user", user);
        return Result.ok(map);
    }

    @GetMapping("/info")
    public Result<User> info() {
        long userId = StpUtil.getLoginIdAsLong();
        return Result.ok(userService.getById(userId));
    }

    @PutMapping("/password")
    public Result<?> updatePassword(@Valid @RequestBody PasswordDto dto) {
        long userId = StpUtil.getLoginIdAsLong();
        userService.updatePassword(userId, dto.getOldPassword(), dto.getNewPassword());
        return Result.ok();
    }

    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody User user) {
        long userId = StpUtil.getLoginIdAsLong();
        userService.updateProfile(userId, user.getNickname(), user.getEmail(), user.getAvatar(), user.getBio(), user.getSocialLinks());
        return Result.ok();
    }
}
