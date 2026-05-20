package com.blog.controller.admin;

import com.blog.common.Result;
import com.blog.entity.Tag;
import com.blog.service.TagService;
import org.springframework.web.bind.annotation.*;

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
