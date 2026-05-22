package com.blog.controller.admin;

import com.blog.common.Result;
import com.blog.dto.ArticleDto;
import com.blog.entity.Article;
import com.blog.service.ArticleService;
import com.blog.service.TagService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/articles")
public class ArticleAdminController {

    private final ArticleService articleService;
    private final TagService tagService;

    public ArticleAdminController(ArticleService articleService, TagService tagService) {
        this.articleService = articleService;
        this.tagService = tagService;
    }

    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        var pageResult = articleService.getAdminList(page, size, status);
        Map<String, Object> map = new HashMap<>();
        map.put("records", pageResult.getRecords());
        map.put("total", pageResult.getTotal());
        return Result.ok(map);
    }

    @GetMapping("/{id}")
    public Result<Article> detail(@PathVariable Long id) {
        Article article = articleService.getById(id);
        if (article != null) {
            article.setTags(tagService.getByArticleId(id));
        }
        return Result.ok(article);
    }

    @PostMapping
    public Result<Article> create(@Valid @RequestBody ArticleDto dto) {
        return Result.ok(articleService.create(dto));
    }

    @PutMapping("/{id}")
    public Result<Article> update(@PathVariable Long id, @Valid @RequestBody ArticleDto dto) {
        return Result.ok(articleService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        articleService.delete(id);
        return Result.ok();
    }
}
