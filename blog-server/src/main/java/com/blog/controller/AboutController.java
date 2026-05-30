package com.blog.controller;

import com.blog.common.Result;
import com.blog.entity.About;
import com.blog.service.AboutService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 公开关于页接口：返回关于页 Markdown 内容和时间线。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/about")
public class AboutController {

    private final AboutService aboutService;

    @GetMapping
    public Result<About> get() {
        return Result.ok(aboutService.get());
    }
}
