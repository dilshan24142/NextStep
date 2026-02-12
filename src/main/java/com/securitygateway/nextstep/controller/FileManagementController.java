package com.securitygateway.nextstep.controller;

import com.securitygateway.nextstep.payload.requests.CreateFolderRequest;
import com.securitygateway.nextstep.service.FileManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "File Management", description = "File and Folder Management APIs")
@SecurityRequirement(name = "bearerAuth")
public class FileManagementController {

    private final FileManagementService fileManagementService;

    // ==================== FOLDER ENDPOINTS ====================

    @Operation(summary = "Create Folder (Admin Only)", description = "Create a new folder or subfolder")
    @PostMapping("/folders/create")
    public ResponseEntity<?> createFolder(
            @Valid @RequestBody CreateFolderRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Create folder request received from user: {}", userDetails.getUsername());
        return fileManagementService.createFolder(request, userDetails.getUsername());
    }

    @Operation(summary = "Delete Folder (Admin Only)", description = "Delete a folder and all its contents")
    @DeleteMapping("/folders/{folderId}")
    public ResponseEntity<?> deleteFolder(
            @PathVariable Long folderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Delete folder request received for folder ID: {} by user: {}", folderId, userDetails.getUsername());
        return fileManagementService.deleteFolder(folderId, userDetails.getUsername());
    }

    @Operation(summary = "Get Folder Details", description = "Get folder information by ID")
    @GetMapping("/folders/{folderId}")
    public ResponseEntity<?> getFolderById(@PathVariable Long folderId) {
        log.info("Get folder request received for folder ID: {}", folderId);
        return fileManagementService.getFolderById(folderId);
    }

    @Operation(summary = "Get Root Folders", description = "Get all top-level folders")
    @GetMapping("/folders/root")
    public ResponseEntity<?> getRootFolders() {
        log.info("Get root folders request received");
        return fileManagementService.getRootFolders();
    }

    @Operation(summary = "Get Subfolders", description = "Get all subfolders of a parent folder")
    @GetMapping("/folders/{parentFolderId}/subfolders")
    public ResponseEntity<?> getSubFolders(@PathVariable Long parentFolderId) {
        log.info("Get subfolders request received for parent folder ID: {}", parentFolderId);
        return fileManagementService.getSubFolders(parentFolderId);
    }

    @Operation(summary = "Search Folders", description = "Search folders by name")
    @GetMapping("/folders/search")
    public ResponseEntity<?> searchFolders(@RequestParam String searchTerm) {
        log.info("Search folders request received with term: {}", searchTerm);
        return fileManagementService.searchFolders(searchTerm);
    }

    // ==================== FILE ENDPOINTS ====================

    @Operation(summary = "Upload File", description = "Upload a file to a specific folder")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("folderId") Long folderId,
            @RequestParam(value = "description", required = false) String description,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Upload file request received: {} to folder: {} by user: {}",
                file.getOriginalFilename(), folderId, userDetails.getUsername());
        return fileManagementService.uploadFile(file, folderId, description, userDetails.getUsername());
    }

    @Operation(summary = "Download File", description = "Download a file by ID")
    @GetMapping("/download/{fileId}")
    public ResponseEntity<?> downloadFile(
            @PathVariable Long fileId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Download file request received for file ID: {} by user: {}", fileId, userDetails.getUsername());
        return fileManagementService.downloadFile(fileId, userDetails.getUsername());
    }

    @Operation(summary = "Delete File", description = "Delete a file (Admin can delete any file, User can delete only own files)")
    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(
            @PathVariable Long fileId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Delete file request received for file ID: {} by user: {}", fileId, userDetails.getUsername());
        return fileManagementService.deleteFile(fileId, userDetails.getUsername());
    }

    // ⭐⭐⭐ FIXED: Pass current user email ⭐⭐⭐
    @Operation(summary = "Get Files in Folder", description = "Get all files in a specific folder")
    @GetMapping("/folders/{folderId}/files")
    public ResponseEntity<?> getFilesInFolder(
            @PathVariable Long folderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Get files request received for folder ID: {} by user: {}", folderId, userDetails.getUsername());
        return fileManagementService.getFilesInFolder(folderId, userDetails.getUsername());
    }

    @Operation(summary = "Search Files", description = "Search files by filename")
    @GetMapping("/search")
    public ResponseEntity<?> searchFiles(
            @RequestParam String searchTerm,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Search files request received with term: {} by user: {}", searchTerm, userDetails.getUsername());
        return fileManagementService.searchFiles(searchTerm, userDetails.getUsername());
    }

    @Operation(summary = "Filter Files by Type", description = "Filter files by file type (e.g., pdf, docx, jpg)")
    @GetMapping("/filter")
    public ResponseEntity<?> filterFilesByType(
            @RequestParam String fileType,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Filter files request received for file type: {} by user: {}", fileType, userDetails.getUsername());
        return fileManagementService.filterFilesByType(fileType, userDetails.getUsername());
    }

    @Operation(summary = "Get My Files", description = "Get all files uploaded by current user")
    @GetMapping("/my-files")
    public ResponseEntity<?> getUserFiles(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Get my files request received from user: {}", userDetails.getUsername());
        return fileManagementService.getUserFiles(userDetails.getUsername());
    }
}