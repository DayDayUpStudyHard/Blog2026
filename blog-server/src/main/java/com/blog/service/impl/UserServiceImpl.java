package com.blog.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.entity.User;
import com.blog.mapper.UserMapper;
import com.blog.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User login(String username, String password) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        return user;
    }

    @Override
    public User getById(Long id) {
        User user = userMapper.selectById(id);
        if (user != null) user.setPassword(null);
        return user;
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null || !BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("旧密码错误");
        }
        user.setPassword(BCrypt.hashpw(newPassword));
        userMapper.updateById(user);
    }

    @Override
    public void updateProfile(Long userId, String nickname, String email, String avatar) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new IllegalArgumentException("用户不存在");
        if (nickname != null) user.setNickname(nickname);
        if (email != null) user.setEmail(email);
        if (avatar != null) user.setAvatar(avatar);
        userMapper.updateById(user);
    }
}
