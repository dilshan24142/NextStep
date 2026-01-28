package com.securitygateway.nextstep.controller;


import com.securitygateway.nextstep.model.FileDocument;
import com.securitygateway.nextstep.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    // Upload (Admin = approved, User = pending)
    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<FileDocument> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("folderId") Long folderId) throws IOException {

        return ResponseEntity.ok(fileService.uploadFile(file, folderId));
    }

    // View approved files (Users + Admin)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<FileDocument>> getApprovedFiles() {
        return ResponseEntity.ok(fileService.getApprovedFiles());
    }

    // Download file
    @GetMapping("/download/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws IOException {

        FileDocument doc = fileService.getApprovedFiles()
                .stream().filter(f -> f.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("File not found"));

        Path path = Path.of(doc.getFilePath());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + doc.getTitle() + "\"")
                .body(resource);
    }
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<FileDocument>> filterFiles(
            @RequestParam String campus,
            @RequestParam String year,
            @RequestParam String faculty,
            @RequestParam String module,
            @RequestParam String semester) {

        return ResponseEntity.ok(
                fileService.filterApprovedFiles(campus, year, faculty, module, semester)
        );
    }

}
