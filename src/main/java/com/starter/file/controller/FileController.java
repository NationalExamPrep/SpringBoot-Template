package com.starter.file.controller;

import com.starter.common.dto.ApiResponse;
import com.starter.file.dto.FileUploadResponse;
import com.starter.file.entity.FileMetadata;
import com.starter.file.service.FileStorageService;
import com.starter.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse;
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
    
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Upload a file",
        description = "Uploads a file to the server. The file is stored securely and associated with the authenticated user."
    )
    @ApiResponses(value = {
        @SwaggerApiResponse(
            responseCode = "200",
            description = "File uploaded successfully",
            content = @Content(schema = @Schema(implementation = FileUploadResponse.class))
        ),
        @SwaggerApiResponse(
            responseCode = "400",
            description = "Invalid file or file too large",
            content = @Content
        ),
        @SwaggerApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content
        )
    })
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadFile(
            @Parameter(description = "File to upload", required = true)
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user
    ) {
        FileUploadResponse response = fileStorageService.storeFile(file, user.getId());
        return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", response));
    }
    
    @GetMapping("/download/{fileName:.+}")
    @Operation(
        summary = "Download a file",
        description = "Downloads a file by its stored filename. Returns the file with appropriate content type and headers."
    )
    @ApiResponses(value = {
        @SwaggerApiResponse(
            responseCode = "200",
            description = "File downloaded successfully",
            content = @Content(mediaType = "application/octet-stream")
        ),
        @SwaggerApiResponse(
            responseCode = "404",
            description = "File not found",
            content = @Content
        ),
        @SwaggerApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content
        )
    })
    public ResponseEntity<Resource> downloadFile(
        @Parameter(description = "Stored filename", required = true, example = "abc123-document.pdf")
        @PathVariable String fileName
    ) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        FileMetadata metadata = fileStorageService.getFileMetadata(fileName);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + metadata.getOriginalFileName() + "\"")
                .body(resource);
    }
    
    @DeleteMapping("/{fileName:.+}")
    @Operation(
        summary = "Delete a file",
        description = "Permanently deletes a file from the server storage and removes its metadata."
    )
    @ApiResponses(value = {
        @SwaggerApiResponse(
            responseCode = "200",
            description = "File deleted successfully",
            content = @Content
        ),
        @SwaggerApiResponse(
            responseCode = "404",
            description = "File not found",
            content = @Content
        ),
        @SwaggerApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content
        )
    })
    public ResponseEntity<ApiResponse<Void>> deleteFile(
        @Parameter(description = "Stored filename to delete", required = true)
        @PathVariable String fileName
    ) {
        fileStorageService.deleteFile(fileName);
        return ResponseEntity.ok(ApiResponse.success("File deleted successfully", null));
    }
}
