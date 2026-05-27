package com.blog.controller;

import com.blog.common.Result;
import com.blog.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * 文件上传接口（需登录态，由 SaToken 拦截器保护）。
 * 根据配置选择本地存储或 S3 兼容存储，返回文件访问 URL。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/upload")
public class UploadController {

    private final FileStorageService storageService;

    @PostMapping
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String url = storageService.store(file);
        return Result.ok(Map.of("url", url));
    }
}
