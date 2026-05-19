package com.blog.service;

import com.blog.entity.Category;
import java.util.List;

public interface CategoryService {
    List<Category> list();
    Category create(Category category);
    Category update(Long id, Category category);
    void delete(Long id);
}
