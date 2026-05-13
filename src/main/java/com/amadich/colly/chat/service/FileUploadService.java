package com.amadich.colly.chat.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final MinioClient minioClient;

    @Value("${minio.url}")
    private String minioUrl;

    public String upload(MultipartFile file, String bucket) throws Exception {

        String filename =
                UUID.randomUUID() +
                        "_" +
                        file.getOriginalFilename();

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(filename)
                        .stream(
                                file.getInputStream(),
                                file.getSize(),
                                -1
                        )
                        .contentType(file.getContentType())
                        .build()
        );

        return minioUrl +
                "/" +
                bucket +
                "/" +
                filename;
    }
}