package com.blog.controller;

import cn.hutool.core.util.IdUtil;
import com.blog.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${blog.upload-path:upload/}")
    private String uploadPath;

    @PostMapping
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = IdUtil.fastSimpleUUID() + ext;
        File dir = new File(uploadPath);
        if (!dir.exists()) dir.mkdirs();
        file.transferTo(new File(dir, fileName));
        Map<String, String> map = new HashMap<>();
        map.put("url", "/upload/" + fileName);
        return Result.ok(map);
    }
}
