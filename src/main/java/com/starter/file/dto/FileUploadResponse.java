package com.starter.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private Long id;
    private String fileName;
    private String originalFileName;
    private String contentType;
    private Long fileSize;
    private String downloadUrl;
    private LocalDateTime uploadedAt;
}
