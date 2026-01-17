package com.securitygateway.nextstep.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/admin/papers")
@CrossOrigin("*")
public class AdminFileController {

    private static final String BASE_DIR = "uploads";

    /* ================= CREATE FOLDER / SUB FOLDER ================= */
    @PostMapping("/create-folder")
    public ResponseEntity<String> createFolder(
            @RequestParam(required = false) String parentPath,
            @RequestParam String folderName) {

        try {
            Path folderPath = (parentPath == null || parentPath.isEmpty())
                    ? Paths.get(BASE_DIR, folderName)
                    : Paths.get(BASE_DIR, parentPath, folderName);

            Files.createDirectories(folderPath);
            return ResponseEntity.ok("Folder created: " + folderPath);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Folder create error: " + e.getMessage());
        }
    }

    /* ================= LIST FOLDERS (ROOT OR SUB) ================= */
    @GetMapping("/list-folders")
    public ResponseEntity<List<String>> listFolders(
            @RequestParam(required = false, defaultValue = "") String path) {

        File dir = Paths.get(BASE_DIR, path).toFile();
        if (!dir.exists() || !dir.isDirectory()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<String> folders = Arrays.stream(Objects.requireNonNull(dir.listFiles(File::isDirectory)))
                .map(File::getName)
                .collect(Collectors.toList());

        return ResponseEntity.ok(folders);
    }

    /* ================= LIST ALL FOLDERS RECURSIVELY ================= */
    @GetMapping("/list-all")
    public ResponseEntity<List<String>> listAllFolders() {
        List<String> result = new ArrayList<>();
        Path base = Paths.get(BASE_DIR);

        try (Stream<Path> paths = Files.walk(base)) {
            paths.filter(Files::isDirectory)
                    .filter(p -> !p.equals(base))
                    .forEach(p -> result.add(base.relativize(p).toString()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(List.of("Error reading folders: " + e.getMessage()));
        }

        return ResponseEntity.ok(result);
    }

    /* ================= UPLOAD FILE ================= */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam String path,
            @RequestParam MultipartFile file) {

        try {
            Path folderPath = Paths.get(BASE_DIR, path);
            Files.createDirectories(folderPath);

            Path filePath = folderPath.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("File uploaded successfully to: " + path);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Upload error: " + e.getMessage());
        }
    }

    /* ================= LIST FILES ================= */
    @GetMapping("/list-files")
    public ResponseEntity<List<String>> listFiles(@RequestParam String path) {

        File dir = Paths.get(BASE_DIR, path).toFile();
        if (!dir.exists() || !dir.isDirectory()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<String> files = Arrays.stream(Objects.requireNonNull(dir.listFiles(File::isFile)))
                .map(File::getName)
                .collect(Collectors.toList());

        return ResponseEntity.ok(files);
    }

    /* ================= DOWNLOAD FILE ================= */
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(
            @RequestParam String path,
            @RequestParam String filename) {

        try {
            Path filePath = Paths.get(BASE_DIR, path, filename);
            if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(filePath.toUri());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /* ================= DELETE FILE ================= */
    @DeleteMapping("/delete-file")
    public ResponseEntity<String> deleteFile(
            @RequestParam String path,
            @RequestParam String filename) {

        try {
            Path filePath = Paths.get(BASE_DIR, path, filename);
            if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                Files.delete(filePath);
                return ResponseEntity.ok("File deleted successfully from: " + path);
            }
            return ResponseEntity.badRequest().body("File not found in: " + path);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Delete error: " + e.getMessage());
        }
    }

    /* ================= DELETE FOLDER (WITH SUB FOLDERS) ================= */
    @DeleteMapping("/delete-folder")
    public ResponseEntity<String> deleteFolder(@RequestParam String path) {

        try {
            Path folderPath = Paths.get(BASE_DIR, path);
            if (!Files.exists(folderPath) || !Files.isDirectory(folderPath)) {
                return ResponseEntity.badRequest().body("Folder not found: " + path);
            }

            Files.walk(folderPath)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

            return ResponseEntity.ok("Folder deleted successfully: " + path);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Delete error: " + e.getMessage());
        }
    }
}
