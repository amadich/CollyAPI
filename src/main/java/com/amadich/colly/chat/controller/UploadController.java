package com.amadich.colly.chat.controller;

import com.amadich.colly.chat.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    private final FileUploadService fileUploadService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> upload(
            @RequestParam("file")
            MultipartFile file,
            @RequestParam String bucket
    ) throws Exception {

        String url = fileUploadService.upload(file,bucket);

        return Map.of(
                "url",
                url
        );
    }
}