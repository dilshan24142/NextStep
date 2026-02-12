package com.securitygateway.nextstep.payload.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderResponse {

    private Long id;
    private String name;
    private String description;
    private String folderPath;
    private Long parentFolderId;
    private String parentFolderName;
    private String createdByEmail;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer subFolderCount;
    private Integer fileCount;
    private List<FolderResponse> subFolders;
}