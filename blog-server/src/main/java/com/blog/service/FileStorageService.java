package com.blog.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

/**
 * 文件存储服务接口，定义统一的文件上传行为。
 * <p>
 * 实现类通过 {@code @ConditionalOnProperty("blog.storage.type")} 条件装配：
 * {@code local} → {@code LocalFileStorageService}，{@code s3} → {@code S3FileStorageService}。
 */
public interface FileStorageService {
    String store(MultipartFile file) throws IOException;
}
