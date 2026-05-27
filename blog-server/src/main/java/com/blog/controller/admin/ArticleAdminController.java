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

/**
 * 后台文章管理（需登录）：CRUD 文章。
 * <p>
 * 重点：创建/更新文章时同步维护 {@code t_article_tag} 关联表；
 * 删除时同时清理关联数据，保持数据一致性。事务由 {@code ArticleServiceImpl} 管理。
 */
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
