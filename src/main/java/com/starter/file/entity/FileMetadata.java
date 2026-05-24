package com.starter.file.entity;

import com.starter.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "file_metadata")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata extends BaseEntity {
    
    @Column(nullable = false)
    private String fileName;
    
    @Column(nullable = false)
    private String originalFileName;
    
    @Column(nullable = false)
    private String contentType;
    
    @Column(nullable = false)
    private Long fileSize;
    
    @Column(nullable = false)
    private String filePath;
    
    private Long uploadedBy;
}
