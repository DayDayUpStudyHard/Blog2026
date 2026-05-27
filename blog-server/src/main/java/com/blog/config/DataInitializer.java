package com.blog.config;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.entity.User;
import com.blog.mapper.UserMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 应用启动时初始化默认管理员账号（如不存在）。
 * <p>
 * 实现 {@link CommandLineRunner}，在 Spring 容器就绪后执行。当前仅检查 admin 用户是否存在，
 * 不存在则创建（密码使用 BCrypt 哈希）。密码硬编码仅用于开发阶段，生产应从环境变量或配置中心注入。
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;

    public DataInitializer(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public void run(String... args) {
        if (!userMapper.exists(new LambdaQueryWrapper<User>().eq(User::getUsername, "admin"))) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(BCrypt.hashpw("admin123"));
            admin.setNickname("管理员");
            userMapper.insert(admin);
        }
    }
}
