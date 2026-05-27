package com.blog.controller.admin;

import com.blog.common.Result;
import com.blog.entity.Tag;
import com.blog.service.TagService;
import org.springframework.web.bind.annotation.*;

/**
 * 后台标签管理（需登录）：创建、更新、删除标签。
 * <p>
 * 注意：更新时手动将路径参数 id 设入 Tag 对象，确保 MyBatis-Plus 按 id 更新。
 */
@RestController
@RequestMapping("/api/admin/tags")
public class TagAdminController {

    private final TagService tagService;

    public TagAdminController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    public Result<Tag> create(@RequestBody Tag tag) {
        return Result.ok(tagService.create(tag));
    }

    @PutMapping("/{id}")
    public Result<Tag> update(@PathVariable Long id, @RequestBody Tag tag) {
        tag.setId(id);
        return Result.ok(tagService.update(tag));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        tagService.delete(id);
        return Result.ok();
    }
}
