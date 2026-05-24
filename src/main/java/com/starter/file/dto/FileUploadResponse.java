package com.starter.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "File upload response")
public class FileUploadResponse {
    
    @Schema(description = "File metadata ID", example = "1")
    private Long id;
    
    @Schema(description = "Stored filename", example = "abc123-document.pdf")
    private String fileName;
    
    @Schema(description = "Original filename", example = "document.pdf")
    private String originalFileName;
    
    @Schema(description = "File content type", example = "application/pdf")
    private String contentType;
    
    @Schema(description = "File size in bytes", example = "1024000")
    private Long fileSize;
    
    @Schema(description = "Download URL", example = "/api/v1/files/download/abc123-document.pdf")
    private String downloadUrl;
    
    @Schema(description = "Upload timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime uploadedAt;
}
