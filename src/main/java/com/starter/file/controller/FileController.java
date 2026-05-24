package com.starter.file.controller;

import com.starter.common.dto.ApiResponse;
import com.starter.file.dto.FileUploadResponse;
import com.starter.file.entity.FileMetadata;
import com.starter.file.service.FileStorageService;
import com.starter.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "File", description = "File management APIs")
public class FileController {
    
    private final FileStorageService fileStorageService;
    
    @PostMapping("/upload")
    @Operation(summary = "Upload a file")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user
    ) {
        FileUploadResponse response = fileStorageService.storeFile(file, user.getId());
        return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", response));
    }
    
    @GetMapping("/download/{fileName:.+}")
    @Operation(summary = "Download a file")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        FileMetadata metadata = fileStorageService.getFileMetadata(fileName);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + metadata.getOriginalFileName() + "\"")
                .body(resource);
    }
    
    @DeleteMapping("/{fileName:.+}")
    @Operation(summary = "Delete a file")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@PathVariable String fileName) {
        fileStorageService.deleteFile(fileName);
        return ResponseEntity.ok(ApiResponse.success("File deleted successfully", null));
    }
}
