package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.entity.Category;
import com.blog.mapper.CategoryMapper;
import com.blog.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类服务实现。
 * <p>
 * 列表按 sort 升序排列；更新时只更新非 null 字段，null 字段保持原值。
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<Category> list() {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort));
    }

    @Override
    public Category create(Category category) {
        categoryMapper.insert(category);
        return category;
    }

    @Override
    public Category update(Long id, Category category) {
        Category existing = categoryMapper.selectById(id);
        if (existing == null) throw new IllegalArgumentException("分类不存在");
        if (category.getName() != null) existing.setName(category.getName());
        if (category.getDescription() != null) existing.setDescription(category.getDescription());
        if (category.getSort() != null) existing.setSort(category.getSort());
        categoryMapper.updateById(existing);
        return existing;
    }

    @Override
    public void delete(Long id) {
        categoryMapper.deleteById(id);
    }
}
