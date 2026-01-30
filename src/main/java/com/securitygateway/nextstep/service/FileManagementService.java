package com.securitygateway.nextstep.service;

import com.securitygateway.nextstep.payload.requests.CreateFolderRequest;
import com.securitygateway.nextstep.payload.responses.FileResponse;
import com.securitygateway.nextstep.payload.responses.FolderResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileManagementService {

    // Folder Operations
    ResponseEntity<?> createFolder(CreateFolderRequest request, String userEmail);
    ResponseEntity<?> deleteFolder(Long folderId, String userEmail);
    ResponseEntity<?> getFolderById(Long folderId);
    ResponseEntity<?> getRootFolders();
    ResponseEntity<?> getSubFolders(Long parentFolderId);
    ResponseEntity<?> searchFolders(String searchTerm);

    // File Operations
    ResponseEntity<?> uploadFile(MultipartFile file, Long folderId, String description, String userEmail);
    ResponseEntity<?> downloadFile(Long fileId, String userEmail);
    ResponseEntity<?> deleteFile(Long fileId, String userEmail);
    ResponseEntity<?> getFilesInFolder(Long folderId);
    ResponseEntity<?> searchFiles(String searchTerm);
    ResponseEntity<?> filterFilesByType(String fileType);
    ResponseEntity<?> getUserFiles(String userEmail);
}