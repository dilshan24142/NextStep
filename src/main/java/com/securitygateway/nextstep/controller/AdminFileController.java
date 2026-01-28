package com.securitygateway.nextstep.controller;


import com.securitygateway.nextstep.model.FileDocument;
import com.securitygateway.nextstep.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/files")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminFileController {

    private final FileService fileService;

    // View pending uploads from users
    @GetMapping("/pending")
    public ResponseEntity<List<FileDocument>> getPendingFiles() {
        return ResponseEntity.ok(fileService.getPendingFiles());
    }

    // Approve file
    @PutMapping("/{id}/approve")
    public ResponseEntity<FileDocument> approveFile(@PathVariable Long id) {
        return ResponseEntity.ok(fileService.approveFile(id));
    }

    // Reject file
    @PutMapping("/{id}/reject")
    public ResponseEntity<FileDocument> rejectFile(@PathVariable Long id) {
        return ResponseEntity.ok(fileService.rejectFile(id));
    }

    // Delete file (Admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFile(@PathVariable Long id) throws IOException {
        fileService.deleteFile(id);
        return ResponseEntity.ok("File deleted successfully");
    }
}

