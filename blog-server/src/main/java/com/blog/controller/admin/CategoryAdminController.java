package com.blog.controller.admin;

import com.blog.common.Result;
import com.blog.entity.Category;
import com.blog.service.CategoryService;
import org.springframework.web.bind.annotation.*;

/**
 * 后台分类管理（需登录）：创建、更新、删除分类。
 */
@RestController
@RequestMapping("/api/admin/categories")
public class CategoryAdminController {

    private final CategoryService categoryService;

    public CategoryAdminController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public Result<Category> create(@RequestBody Category category) {
        return Result.ok(categoryService.create(category));
    }

    @PutMapping("/{id}")
    public Result<Category> update(@PathVariable Long id, @RequestBody Category category) {
        return Result.ok(categoryService.update(id, category));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.ok();
    }
}
