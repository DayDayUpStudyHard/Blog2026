package com.blog.service.impl;

import cn.hutool.core.util.IdUtil;
import com.blog.config.FileStorageConfig;
import com.blog.service.FileStorageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;

@Service
@ConditionalOnProperty(name = "blog.storage.type", havingValue = "s3")
public class S3FileStorageService implements FileStorageService {

    private final S3Client client;
    private final FileStorageConfig config;

    public S3FileStorageService(FileStorageConfig config) {
        this.config = config;
        var s3Config = config.getS3();
        this.client = S3Client.builder()
                .region(Region.of(s3Config.getRegion()))
                .endpointOverride(URI.create(s3Config.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(s3Config.getAccessKey(), s3Config.getSecretKey())))
                .forcePathStyle(true)
                .build();
    }

    @Override
    public String store(MultipartFile file) throws IOException {
        var s3 = config.getS3();
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String key = "blog/" + IdUtil.fastSimpleUUID() + ext;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(s3.getBucket())
                .key(key)
                .contentType(file.getContentType())
                .build();

        client.putObject(request, RequestBody.fromBytes(file.getBytes()));

        if (s3.getPublicUrl() != null && !s3.getPublicUrl().isBlank()) {
            return s3.getPublicUrl() + "/" + key;
        }
        return s3.getEndpoint() + "/" + s3.getBucket() + "/" + key;
    }
}
