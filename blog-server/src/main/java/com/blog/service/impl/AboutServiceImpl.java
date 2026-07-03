package com.blog.service.impl;

import com.blog.entity.About;
import com.blog.mapper.AboutMapper;
import com.blog.service.AboutService;
import lombok.RequiredArgsConstructor;
import com.blog.annotation.CacheShield;
import com.blog.annotation.CacheShieldEvict;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AboutServiceImpl implements AboutService {

    private final AboutMapper aboutMapper;

    @CacheShield(value = "about", key = "'about'", ttl = 30, ttlVariance = 10)
    @Override
    public About get() {
        About about = aboutMapper.selectById(1L);
        if (about == null) {
            about = new About();
            about.setId(1L);
            about.setContent("# 关于我\n\n感谢你的访问。");
            about.setTimeline("[]");
            about.setUpdateTime(LocalDateTime.now());
            aboutMapper.insert(about);
        }
        return about;
    }

    @CacheShieldEvict(value = "about", key = "'about'")
    @Override
    public void update(String content, String timeline) {
        About about = aboutMapper.selectById(1L);
        boolean exists = (about != null);
        if (!exists) {
            about = new About();
            about.setId(1L);
        }
        if (content != null) about.setContent(content);
        if (timeline != null) about.setTimeline(timeline);
        about.setUpdateTime(LocalDateTime.now());
        if (exists) {
            aboutMapper.updateById(about);
        } else {
            aboutMapper.insert(about);
        }
    }
}
