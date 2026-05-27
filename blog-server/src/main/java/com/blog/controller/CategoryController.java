package com.blog.controller;

import com.blog.common.Result;
import com.blog.entity.Category;
import com.blog.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台分类接口（公开）：返回所有分类列表。
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public Result<List<Category>> list() {
        return Result.ok(categoryService.list());
    }
}
