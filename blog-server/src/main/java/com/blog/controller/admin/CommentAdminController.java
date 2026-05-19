package com.blog.controller.admin;

import com.blog.common.Result;
import com.blog.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/comments")
public class CommentAdminController {

    private final CommentService commentService;

    public CommentAdminController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        var pageResult = commentService.getAdminList(page, size, status);
        Map<String, Object> map = new HashMap<>();
        map.put("records", pageResult.getRecords());
        map.put("total", pageResult.getTotal());
        return Result.ok(map);
    }

    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        commentService.updateStatus(id, body.get("status"));
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        commentService.delete(id);
        return Result.ok();
    }
}
