package com.starter.file.service;

import com.starter.common.exception.BadRequestException;
import com.starter.common.exception.ResourceNotFoundException;
import com.starter.file.dto.FileUploadResponse;
import com.starter.file.entity.FileMetadata;
import com.starter.file.repository.FileMetadataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {
    
    private final Path fileStorageLocation;
    private final FileMetadataRepository fileMetadataRepository;
    private final long maxFileSize;
    
    public FileStorageService(
            @Value("${app.file.upload-dir}") String uploadDir,
            @Value("${app.file.max-file-size}") long maxFileSize,
            FileMetadataRepository fileMetadataRepository
    ) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.maxFileSize = maxFileSize;
        this.fileMetadataRepository = fileMetadataRepository;
        
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create upload directory", ex);
        }
    }
    
    @Transactional
    public FileUploadResponse storeFile(MultipartFile file, Long userId) {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }
        
        if (file.getSize() > maxFileSize) {
            throw new BadRequestException("File size exceeds maximum limit of " + maxFileSize + " bytes");
        }
        
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        
        try {
            if (originalFileName.contains("..")) {
                throw new BadRequestException("Invalid file path: " + originalFileName);
            }
            
            String fileExtension = "";
            int dotIndex = originalFileName.lastIndexOf('.');
            if (dotIndex > 0) {
                fileExtension = originalFileName.substring(dotIndex);
            }
            
            String fileName = UUID.randomUUID().toString() + fileExtension;
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            FileMetadata metadata = FileMetadata.builder()
                    .fileName(fileName)
                    .originalFileName(originalFileName)
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .filePath(targetLocation.toString())
                    .uploadedBy(userId)
                    .build();
            
            metadata = fileMetadataRepository.save(metadata);
            
            String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/v1/files/download/")
                    .path(fileName)
                    .toUriString();
            
            log.info("File uploaded successfully: {}", fileName);
            
            return FileUploadResponse.builder()
                    .id(metadata.getId())
                    .fileName(fileName)
                    .originalFileName(originalFileName)
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .downloadUrl(downloadUrl)
                    .uploadedAt(metadata.getCreatedAt())
                    .build();
            
        } catch (IOException ex) {
            log.error("Failed to store file: {}", originalFileName, ex);
            throw new RuntimeException("Failed to store file", ex);
        }
    }
    
    @Transactional(readOnly = true)
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found: " + fileName);
            }
        } catch (Exception ex) {
            throw new ResourceNotFoundException("File not found: " + fileName);
        }
    }
    
    @Transactional(readOnly = true)
    public FileMetadata getFileMetadata(String fileName) {
        return fileMetadataRepository.findByFileName(fileName)
                .orElseThrow(() -> new ResourceNotFoundException("File metadata not found: " + fileName));
    }
    
    @Transactional
    public void deleteFile(String fileName) {
        try {
            FileMetadata metadata = getFileMetadata(fileName);
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
            fileMetadataRepository.delete(metadata);
            log.info("File deleted successfully: {}", fileName);
        } catch (IOException ex) {
            log.error("Failed to delete file: {}", fileName, ex);
            throw new RuntimeException("Failed to delete file", ex);
        }
    }
}
