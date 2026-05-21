package com.blog.service.impl;

import cn.hutool.core.util.IdUtil;
import com.blog.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

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
