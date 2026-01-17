package com.securitygateway.nextstep.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.StandardCopyOption;


import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/user/papers")
@CrossOrigin("*")
public class UserFileController {

    private static final String BASE_DIR = "uploads";

    /* ---------- LIST MAIN FOLDERS ---------- */
    @GetMapping("/list-folders")
    public ResponseEntity<List<String>> listFolders() {
        File baseDir = new File(BASE_DIR);
        if (!baseDir.exists()) baseDir.mkdirs();

        List<String> folders = Arrays.stream(baseDir.listFiles(File::isDirectory))
                .map(File::getName)
                .collect(Collectors.toList());

        return ResponseEntity.ok(folders);
    }

    /* ---------- LIST FILES IN FOLDER ---------- */
    @GetMapping("/list/{folder}")
    public ResponseEntity<List<String>> listFiles(@PathVariable String folder) {
        File dir = new File(BASE_DIR + "/" + folder);
        if (!dir.exists() || !dir.isDirectory()) return ResponseEntity.ok(List.of());

        List<String> files = Arrays.stream(dir.listFiles(File::isFile))
                .map(File::getName)
                .collect(Collectors.toList());

        return ResponseEntity.ok(files);
    }

    /* ---------- DOWNLOAD FILE ---------- */
    @GetMapping("/download/{folder}/{fileName}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String folder,
            @PathVariable String fileName) {

        try {
            Path filePath = Paths.get(BASE_DIR)
                    .resolve(folder)
                    .resolve(fileName)
                    .normalize();

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /* ---------- LIST ALL FOLDERS + SUBFOLDERS RECURSIVELY ---------- */
    @GetMapping("/list-all")
    public ResponseEntity<List<String>> listAllFoldersAndFiles() {
        List<String> result = new ArrayList<>();
        Path base = Paths.get(BASE_DIR);

        try (Stream<Path> paths = Files.walk(base)) {
            paths.filter(p -> !p.equals(base))
                    .forEach(p -> result.add(base.relativize(p).toString()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(List.of("Error reading folders/files"));
        }

        return ResponseEntity.ok(result);
    }
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam String folder,
            @RequestParam MultipartFile file) {

        try {
            Path folderPath = Paths.get(BASE_DIR, folder);
            Files.createDirectories(folderPath);

            Path filePath = folderPath.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // ---- ADMIN NOTIFICATION (simple) ----
            System.out.println("ADMIN NOTIFICATION: User uploaded file -> "
                    + file.getOriginalFilename() + " to folder -> " + folder);

            return ResponseEntity.ok("File uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Upload failed: " + e.getMessage());
        }
    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(
            @RequestParam String folder,
            @RequestParam String filename) {

        try {
            Path filePath = Paths.get(BASE_DIR, folder, filename);

            if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                Files.delete(filePath);

                // ---- ADMIN NOTIFICATION ----
                System.out.println("ADMIN NOTIFICATION: User deleted file -> "
                        + filename + " from folder -> " + folder);

                return ResponseEntity.ok("File deleted successfully");
            }

            return ResponseEntity.badRequest().body("File not found");

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Delete failed: " + e.getMessage());
        }
    }


}

