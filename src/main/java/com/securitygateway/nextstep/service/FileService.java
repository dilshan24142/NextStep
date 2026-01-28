package com.securitygateway.nextstep.service;

import com.securitygateway.nextstep.model.FileDocument;
import com.securitygateway.nextstep.model.FileStatus;
import com.securitygateway.nextstep.model.Folder;
import com.securitygateway.nextstep.model.User;
import com.securitygateway.nextstep.repository.FileDocumentRepository;
import com.securitygateway.nextstep.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileDocumentRepository fileRepository;
    private final FolderRepository folderRepository;

    private final String uploadDir = "uploads/modelpapers/";
    private final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    public FileDocument uploadFile(MultipartFile file, Long folderId) throws IOException {

        // 🔒 Validate file not empty
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        // 🔒 Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File size must be less than 10MB");
        }

        // 🔒 Validate content type
        if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
            throw new RuntimeException("Only PDF files are allowed");
        }

        // 🔒 Validate file extension
        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.toLowerCase().endsWith(".pdf")) {
            throw new RuntimeException("Invalid file type. Only PDF allowed.");
        }

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        // Ensure upload directory exists
        Files.createDirectories(Paths.get(uploadDir));

        // Prevent filename conflicts
        String storedFileName = System.currentTimeMillis() + "_" + originalName.replaceAll("\\s+", "_");
        Path filePath = Paths.get(uploadDir).resolve(storedFileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        FileStatus status = currentUser.getRole().name().equals("ADMIN")
                ? FileStatus.APPROVED
                : FileStatus.PENDING;

        FileDocument document = FileDocument.builder()
                .title(originalName)
                .fileName(storedFileName)
                .filePath(filePath.toString())
                .status(status)
                .uploadDate(LocalDateTime.now())
                .uploadedBy(currentUser)
                .folder(folder)
                .build();

        return fileRepository.save(document);
    }

    public List<FileDocument> getApprovedFiles() {
        return fileRepository.findByStatus(FileStatus.APPROVED);
    }

    public List<FileDocument> getPendingFiles() {
        return fileRepository.findByStatus(FileStatus.PENDING);
    }

    public void deleteFile(Long id) throws IOException {
        FileDocument doc = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        Files.deleteIfExists(Paths.get(doc.getFilePath()));
        fileRepository.delete(doc);
    }

    public FileDocument approveFile(Long id) {
        FileDocument doc = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        doc.setStatus(FileStatus.APPROVED);
        return fileRepository.save(doc);
    }

    public FileDocument rejectFile(Long id) {
        FileDocument doc = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        doc.setStatus(FileStatus.REJECTED);
        return fileRepository.save(doc);
    }

    public List<FileDocument> filterApprovedFiles(String campus, String year, String faculty, String module, String semester) {
        return fileRepository
                .findByFolder_CampusAndFolder_YearAndFolder_FacultyAndFolder_ModuleAndFolder_SemesterAndStatus(
                        campus, year, faculty, module, semester, FileStatus.APPROVED);
    }
}
