package com.securitygateway.nextstep.Dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileResponse {

    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String fileSizeFormatted; // "2.5 MB"
    private String description;
    private Long folderId;
    private String folderName;
    private String folderPath;
    private String uploadedByEmail;
    private String uploadedByName;
    private LocalDateTime uploadedAt;
    private Boolean canDelete; // true if current user can delete
}