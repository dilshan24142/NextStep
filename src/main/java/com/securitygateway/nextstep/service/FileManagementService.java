package com.securitygateway.nextstep.service;

import com.securitygateway.nextstep.Dtos.requests.CreateFolderRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FileManagementService {

    // Folder operations
    ResponseEntity<?> createFolder(CreateFolderRequest request, String userEmail);
    ResponseEntity<?> deleteFolder(Long folderId, String userEmail);
    ResponseEntity<?> getFolderById(Long folderId);
    ResponseEntity<?> getRootFolders();
    ResponseEntity<?> getSubFolders(Long parentFolderId);
    ResponseEntity<?> searchFolders(String searchTerm);

    // File operations
    ResponseEntity<?> uploadFile(MultipartFile file, Long folderId, String description, String userEmail);
    ResponseEntity<?> downloadFile(Long fileId, String userEmail);
    ResponseEntity<?> deleteFile(Long fileId, String userEmail);

    // ⭐ FIXED: Added userEmail parameter
    ResponseEntity<?> getFilesInFolder(Long folderId, String userEmail);
    ResponseEntity<?> searchFiles(String searchTerm, String userEmail);
    ResponseEntity<?> filterFilesByType(String fileType, String userEmail);
    ResponseEntity<?> getUserFiles(String userEmail);
}