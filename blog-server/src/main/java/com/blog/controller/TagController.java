package com.blog.controller;

import com.blog.common.Result;
import com.blog.entity.Tag;
import com.blog.service.TagService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台标签接口（公开）：返回所有标签列表。
 */
@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public Result<List<Tag>> list() {
        return Result.ok(tagService.list());
    }
}
