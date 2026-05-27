package com.blog.service;

import com.blog.entity.Category;
import java.util.List;

/**
 * 分类服务接口。
 */
public interface CategoryService {
    List<Category> list();
    Category create(Category category);
    Category update(Long id, Category category);
    void delete(Long id);
}
