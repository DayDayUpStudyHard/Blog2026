package com.blog.service.impl;

import cn.hutool.core.util.IdUtil;
import com.blog.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 本地文件存储实现，默认生效。
 * <p>
 * 文件保存至配置的 {@code blog.upload-path} 目录（默认 {@code upload/}），
 * 返回相对路径 {@code /upload/xxx.ext}，由 {@code WebConfig} 映射为静态资源。
 * 文件名使用 UUID 避免冲突。
 */
@Service
@ConditionalOnProperty(name = "blog.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalFileStorageService implements FileStorageService {

    @Value("${blog.upload-path:upload/}")
    private String uploadPath;

    @Override
    public String store(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = IdUtil.fastSimpleUUID() + ext;
        File dir = new File(uploadPath);
        if (!dir.exists()) dir.mkdirs();
        file.transferTo(new File(dir, fileName));
        return "/upload/" + fileName;
    }
}
