package com.securitygateway.nextstep.controller;

import com.securitygateway.nextstep.model.FileMeta;
import com.securitygateway.nextstep.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/files")
@RequiredArgsConstructor
public class UserFileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<FileMeta> uploadFile(@RequestParam MultipartFile file,
                                               @RequestParam Long folderId,
                                               @RequestParam String email) {
        return ResponseEntity.ok(fileService.uploadFile(file, folderId, email, false));
    }

    @DeleteMapping("/delete-file/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable Long fileId,
                                             @RequestParam String email) {
        fileService.deleteFile(fileId, email, false);
        return ResponseEntity.ok("File deleted successfully");
    }

    @GetMapping("/list-files")
    public ResponseEntity<List<FileMeta>> listFiles(@RequestParam Long folderId) {
        return ResponseEntity.ok(fileService.listFiles(folderId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<FileMeta>> searchFiles(@RequestParam String keyword) {
        return ResponseEntity.ok(fileService.searchFiles(keyword));
    }
}
