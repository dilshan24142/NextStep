package com.securitygateway.nextstep.controller;

import com.securitygateway.nextstep.model.FileMeta;
import com.securitygateway.nextstep.model.Folder;
import com.securitygateway.nextstep.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    // Helper method එකක් පාවිච්චි කරලා Admin ද නැද්ද කියලා check කරන එක ලේසියි
    private boolean isAdmin(UserDetails user) {
        return user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") ||
                        auth.getAuthority().equals("ADMIN"));
    }

    // ================= Admin Actions =================

    @PostMapping("/create-folder")
    public ResponseEntity<Folder> createFolder(@RequestParam String folderName,
                                               @RequestParam(required = false) Long parentId,
                                               @AuthenticationPrincipal UserDetails user) {
        if (!isAdmin(user)) {
            throw new RuntimeException("Access Denied: Only admins can create folders");
        }
        return ResponseEntity.ok(fileService.createFolder(folderName, parentId));
    }

    @PostMapping("/upload-admin")
    public ResponseEntity<FileMeta> uploadAdminFile(@RequestParam MultipartFile file,
                                                    @RequestParam Long folderId,
                                                    @AuthenticationPrincipal UserDetails user) {
        if (!isAdmin(user)) {
            throw new RuntimeException("Access Denied: Only admins can upload admin files");
        }
        return ResponseEntity.ok(fileService.uploadFile(file, folderId, user.getUsername(), true));
    }

    @DeleteMapping("/delete-admin-file/{fileId}")
    public ResponseEntity<String> deleteAdminFile(@PathVariable Long fileId,
                                                  @AuthenticationPrincipal UserDetails user) {
        if (!isAdmin(user)) {
            throw new RuntimeException("Access Denied: Only admins can delete admin files");
        }
        fileService.deleteFile(fileId, user.getUsername(), true);
        return ResponseEntity.ok("Admin file deleted successfully");
    }

    @DeleteMapping("/delete-folder/{folderId}")
    public ResponseEntity<String> deleteFolder(@PathVariable Long folderId,
                                               @AuthenticationPrincipal UserDetails user) {
        if (!isAdmin(user)) {
            throw new RuntimeException("Access Denied: Only admins can delete folders");
        }
        fileService.deleteFolder(folderId);
        return ResponseEntity.ok("Folder deleted successfully");
    }

    // ================= User Actions =================

    @PostMapping("/upload")
    public ResponseEntity<FileMeta> uploadFile(@RequestParam MultipartFile file,
                                               @RequestParam Long folderId,
                                               @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(fileService.uploadFile(file, folderId, user.getUsername(), false));
    }

    @DeleteMapping("/delete-file/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable Long fileId,
                                             @AuthenticationPrincipal UserDetails user) {
        fileService.deleteFile(fileId, user.getUsername(), false);
        return ResponseEntity.ok("File deleted successfully");
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileMeta>> listFiles(@RequestParam Long folderId) {
        return ResponseEntity.ok(fileService.listFiles(folderId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<FileMeta>> searchFiles(@RequestParam String keyword) {
        return ResponseEntity.ok(fileService.searchFiles(keyword));
    }
}