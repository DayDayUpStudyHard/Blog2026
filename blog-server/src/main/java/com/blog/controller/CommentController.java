package com.blog.controller;

import com.blog.common.Result;
import com.blog.dto.CommentDto;
import com.blog.entity.Comment;
import com.blog.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/articles/{articleId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public Result<Map<String, Object>> list(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageResult = commentService.getByArticleId(articleId, page, size);
        Map<String, Object> map = new HashMap<>();
        map.put("records", pageResult.getRecords());
        map.put("total", pageResult.getTotal());
        return Result.ok(map);
    }

    @PostMapping
    public Result<Comment> create(@PathVariable Long articleId, @Valid @RequestBody CommentDto dto) {
        return Result.ok(commentService.create(articleId, dto));
    }
}
