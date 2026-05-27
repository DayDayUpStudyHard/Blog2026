package com.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.common.Result;
import com.blog.entity.Article;
import com.blog.entity.Tag;
import com.blog.service.ArticleService;
import com.blog.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 前台文章接口（无需登录）：文章列表、详情、上下篇导航。
 * <p>
 * 列表仅返回已发布(status=1)的文章；详情页会自增阅读量。
 * 列表接口返回 {@code content} 字段为空以减少传输量，详情接口返回完整内容。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final TagService tagService;

    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        Page<Article> pageResult = articleService.getPublishedList(page, size, categoryId, keyword);
        Map<String, Object> map = new HashMap<>();
        map.put("records", pageResult.getRecords());
        map.put("total", pageResult.getTotal());
        return Result.ok(map);
    }

    @GetMapping("/{id}")
    public Result<Article> detail(@PathVariable Long id) {
        Article article = articleService.getDetail(id);
        return Result.ok(article);
    }

    @GetMapping("/{id}/nav")
    public Result<Map<String, Object>> nav(@PathVariable Long id) {
        return Result.ok(articleService.getNav(id));
    }
}
