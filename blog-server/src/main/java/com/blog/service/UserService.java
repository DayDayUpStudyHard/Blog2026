package com.blog.service;

import com.blog.entity.User;

public interface UserService {
    User login(String username, String password);
    User getById(Long id);
    void updatePassword(Long userId, String oldPassword, String newPassword);
    void updateProfile(Long userId, String nickname, String email, String avatar);
}
