package com.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.Moment;

public interface MomentService {
    Page<Moment> list(int page, int size);
    Moment create(Moment moment);
    Moment update(Long id, Moment moment);
    void delete(Long id);
}
