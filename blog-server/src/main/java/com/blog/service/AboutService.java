package com.blog.service;

import com.blog.entity.About;

public interface AboutService {
    About get();
    void update(String content, String timeline);
}
