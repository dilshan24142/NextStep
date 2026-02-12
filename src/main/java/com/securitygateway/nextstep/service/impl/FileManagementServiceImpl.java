package com.securitygateway.nextstep.service.impl;

import com.securitygateway.nextstep.exceptions.ResourceNotFoundException;
import com.securitygateway.nextstep.model.*;
import com.securitygateway.nextstep.Dtos.requests.CreateFolderRequest;
import com.securitygateway.nextstep.Dtos.responses.FileResponse;
import com.securitygateway.nextstep.Dtos.responses.FolderResponse;
import com.securitygateway.nextstep.Dtos.responses.GeneralAPIResponse;
import com.securitygateway.nextstep.repository.FileRepository;
import com.securitygateway.nextstep.repository.FolderRepository;
import com.securitygateway.nextstep.repository.UserRepository;
import com.securitygateway.nextstep.service.EmailService;
import com.securitygateway.nextstep.service.FileManagementService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.securitygateway.nextstep.service.FileNotificationService;


@Service
@RequiredArgsConstructor
@Slf4j
public class FileManagementServiceImpl implements FileManagementService {

    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final FileNotificationService fileNotificationService;

    private static final String UPLOAD_DIR = "uploads/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    // ==================== FOLDER OPERATIONS ====================

    @Override
    @Transactional
    public ResponseEntity<?> createFolder(CreateFolderRequest request, String userEmail) {
        try {
            log.info("Creating folder '{}' by user {}", request.getName(), userEmail);

            User user = userRepository.findByEmail(userEmail.trim().toLowerCase())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            // Check if user is admin
            if (user.getRole() != Role.ADMIN) {
                log.warn("User {} attempted to create folder without admin privileges", userEmail);
                return new ResponseEntity<>(
                        GeneralAPIResponse.builder()
                                .message("Only admins can create folders")
                                .build(),
                        HttpStatus.FORBIDDEN
                );
            }

            Folder parentFolder = null;
            if (request.getParentFolderId() != null) {
                parentFolder = folderRepository.findById(request.getParentFolderId())
                        .orElseThrow(() -> new ResourceNotFoundException("Parent folder not found"));
            }

            // Check if folder with same name exists under parent
            if (folderRepository.existsByNameAndParentFolder(request.getName(), parentFolder)) {
                return new ResponseEntity<>(
                        GeneralAPIResponse.builder()
                                .message("Folder with this name already exists in this location")
                                .build(),
                        HttpStatus.BAD_REQUEST
                );
            }

            Folder folder = Folder.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .parentFolder(parentFolder)
                    .createdBy(user)
                    .build();

            folder = folderRepository.save(folder);
            log.info("Folder created successfully with ID: {}", folder.getId());

            return new ResponseEntity<>(
                    convertToFolderResponse(folder, user),
                    HttpStatus.CREATED
            );

        } catch (ResourceNotFoundException ex) {
            log.error("Resource not found: {}", ex.getMessage());
            return new ResponseEntity<>(
                    GeneralAPIResponse.builder()
                            .message(ex.getMessage())
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        } catch (Exception ex) {
            log.error("Error creating folder", ex);
            return new ResponseEntity<>(
                    GeneralAPIResponse.builder()
                            .message("Failed to create folder: " + ex.getMessage())
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteFolder(Long folderId, String userEmail) {
        try {
            log.info("Deleting folder ID {} by user {}", folderId, userEmail);

            User user = userRepository.findByEmail(userEmail.trim().toLowerCase())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            if (user.getRole() != Role.ADMIN) {
                log.warn("User {} attempted to delete folder without admin privileges", userEmail);
                return new ResponseEntity<>(
                        GeneralAPIResponse.builder()
                                .message("Only admins can delete folders")
                                .build(),
                        HttpStatus.FORBIDDEN
                );
            }

            Folder folder = folderRepository.findById(folderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Folder not found"));

            // Delete all files in folder and subfolders physically
            deleteAllFilesInFolder(folder);

            // Delete folder (cascade will delete subfolders and file records)
            folderRepository.delete(folder);
            log.info("Folder {} deleted successfully", folderId);

            return new ResponseEntity<>(
                    GeneralAPIResponse.builder()
                            .message("Folder deleted successfully")
                            .build(),
                    HttpStatus.OK
            );

        } catch (ResourceNotFoundException ex) {
            log.error("Resource not found: {}", ex.getMessage());
            return new ResponseEntity<>(
                    GeneralAPIResponse.builder()
                            .message(ex.getMessage())
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        } catch (Exception ex) {
            log.error("Error deleting folder", ex);
            return new ResponseEntity<>(
                    GeneralAPIResponse.builder()
                            .message("Failed to delete folder: " + ex.getMessage())
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public ResponseEntity<?> getFolderById(Long folderId) {
        try {
            Folder folder = folderRepository.findById(folderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Folder not found"));

            return new ResponseEntity<>(
                    convertToFolderResponse(folder, folder.getCreatedBy()),
                    HttpStatus.OK
            );

        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(
                    GeneralAPIResponse.builder()
                            .message(ex.getMessage())
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @Override
    public ResponseEntity<?> getRootFolders() {
        try {
            List<Folder> rootFolders = folderRepository.findByParentFolderIsNull();

            List<FolderResponse> responses = rootFolders.stream()
                    .map(folder -> convertToFolderResponse(folder, folder.getCreatedBy()))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(responses, HttpStatus.OK);

        } catch (Exception ex) {
            log.error("Error getting root folders", ex);
            return new ResponseEntity<>(
                    GeneralAPIResponse.builder()
                            .message("Failed to get root folders")
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public ResponseEntity<?> getSubFolders(Long parentFolderId) {
        try {
            Folder parentFolder = folderRepository.findById(parentFolderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent folder not found"));

            List<Folder> subFolders = folderRepository.findByParentFolder(parentFolder);

            List<FolderResponse> responses = subFolders.stream()
                    .map(folder -> convertToFolderResponse(folder, folder.getCreatedBy()))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(responses, HttpStatus.OK);

        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(
                    GeneralAPIResponse.builder()
                            .message(ex.getMessage())
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @Override
    public ResponseEntity<?> searchFolders(String searchTerm) {
        try {
            List<Folder> folders = folderRepository.searchByName(searchTerm);

            List<FolderResponse> responses = folders.stream()
                    .map(folder -> convertToFolderResponse(folder, folder.getCreatedBy()))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(responses, HttpStatus.OK);

        } catch (Exception ex) {
            log.error("Error searching folders", ex);
            return new ResponseEntity<>(
                    GeneralAPIResponse.builder()
                            .message("Failed to search folders")
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // ==================== FILE OPERATIONS ====================

    @Override
    @Transactional
    public ResponseEntity<?> uploadFile(MultipartFile file, Long folderId, String description, String userEmail) {
        try {
            log.info("Uploading file '{}' to folder {} by user {}", file.getOriginalFilename(), folderId, userEmail);

            if (file.isEmpty()) {
                return new ResponseEntity<>(
                        GeneralAPIResponse.builder()
                                .message("File is empty")
                                .build(),
                        HttpStatus.BAD_REQUEST
                );
            }

            if (file.getSize() > MAX_FILE_SIZE) {
                return new ResponseEntity<>(
                        GeneralAPIResponse.builder()
                                .message("File size exceeds maximum limit of 10MB")
                                .build(),
                        HttpStatus.BAD_REQUEST
                );
            }

            User user = userRepository.findByEmail(userEmail.trim().toLowerCase())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            Folder folder = folderRepository.findById(folderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Folder not found"));

            // Create unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // Create upload directory if not exists
            Path uploadPath = Paths.get(UPLOAD_DIR + folder.getFolderPath());
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Save file
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Save file metadata
            FileEntity fileEntity = FileEntity.builder()
                    .fileName(originalFilename)
                    .fileType(fileExtension.substring(1))
                    .fileSize(file.getSize())
                    .filePath(filePath.toString())
                    .description(description)
                    .folder(folder)
                    .uploadedBy(user)
                    .isActive(true)
                    .build();

            fileEntity = fileRepository.save(fileEntity);
            log.info("File uploaded successfully with ID: {}", fileEntity.getId());

            // Send email to admin if user uploaded
            if (user.getRole() == Role.USER) {
                sendFileUploadNotificationToAdmin(user, fileEntity, folder);
            }

            return new ResponseEntity<>(
                    convertToFileResponse(fileEntity, user),
                    HttpStatus.CREATED
            );

        } catch (ResourceNotFoundException ex) {
            log.error("Resource not found: {}", ex.getMessage());
            return new ResponseEntity<>(
                    GeneralAPIResponse.builder()
                            .message(ex.getMessage())
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        } catch (IOException ex) {
            log.error("Error saving file", ex);
            return new ResponseEntity<>(
                    GeneralAPIResponse.builder()
                            .message("Failed to save file: " + ex.getMessage())
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        } catch (Exception ex) {
            log.error("Error uploading file", ex);
            return new ResponseEntity<>(
                    GeneralAPIResponse.builder()
                            .message("Failed to upload file: " + ex.getMessage())
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public ResponseEntity<?> downloadFile(Long fileId, String userEmail) {
        try {
            log.info("Downloading file ID {} by user {}", fileId, userEmail);

            User user = userRepository.findByEmail(userEmail.trim().toLowerCase())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            FileEntity fileEntity = fileRepository.findById(fileId)
                    .orElseThrow(() -> new ResourceNotFoundException("File not found"));

            if (!fileEntity.getIsActive()) {
                return new ResponseEntity<>(
                        GeneralAPIResponse.builder()
                                .message("File is not available")
                                .build(),
                        HttpStatus.NOT_FOUND
                );
            }

            Path filePath = Paths.get(fileEntity.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return new ResponseEntity<>(
                        GeneralAPIResponse.builder()
                                .message("File not found on server")
                                .build(),
                        HttpStatus.NOT_FOUND
                );
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileEntity.getFileName() + "\"")
                    .body(resource);

        } catch (ResourceNotFoundException ex) {
            log.error("Resource not found: {}", ex.getMessage());
            return new ResponseEntity<>(
                    GeneralAPIResponse.builder()
                            .message(ex.getMessage())
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        } catch (Exception ex) {
            log.error("Error downloading file", ex);
            return new ResponseEntity<>(
                    GeneralAPIResponse.builder()
                            .message("Failed to download file: " + ex.getMessage())
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteFile(Long fileId, String userEmail) {
        try {
            log.info("Deleting file ID {} by user {}", fileId, userEmail);

            User user = userRepository.findByEmail(userEmail.trim().toLowerCase())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            FileEntity fileEntity = fileRepository.findById(fileId)
                    .orElseThrow(() -> new ResourceNotFoundException("File not found"));

            // Check permissions
            boolean canDelete = false;
            if (user.getRole() == Role.ADMIN) {
                canDelete = true;
            } else if (user.getRole() == Role.USER && fileEntity.getUploadedBy().getId().equals(user.getId())) {
                canDelete = true;
            }

            if (!canDelete) {
                return new ResponseEntity<>(
                        GeneralAPIResponse.builder()
                                .message("You don't have permission to delete this file")
                                .build(),
                        HttpStatus.FORBIDDEN
                );
            }

            // Delete physical file
            try {
                Path filePath = Paths.get(fileEntity.getFilePath());
                Files.deleteIfExists(filePath);
            } catch (IOException ex) {
                log.error("Error deleting physical file", ex);
            }

            // Delete from database
            fileRepository.delete(fileEntity);
            log.info("File {} deleted successfully", fileId);

            return new ResponseEntity<>(
                    GeneralAPIResponse.builder()
                            .message("File deleted successfully")
                            .build(),
                    HttpStatus.OK
            );

        } catch (ResourceNotFoundException ex) {
            log.error("Resource not found: {}", ex.getMessage());
            return new ResponseEntity<>(
                    GeneralAPIResponse.builder()
                            .message(ex.getMessage())
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        } catch (Exception ex) {
            log.error("Error deleting file", ex);
            return new ResponseEntity<>(
                    GeneralAPIResponse.builder()
                            .message("Failed to delete file: " + ex.getMessage())
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public ResponseEntity<?> getFilesInFolder(Long folderId) {
        try {
            Folder folder = folderRepository.findById(folderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Folder not found"));

            List<FileEntity> files = fileRepository.findByFolderAndIsActiveTrue(folder);

            List<FileResponse> responses = files.stream()
                    .map(file -> convertToFileResponse(file, file.getUploadedBy()))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(responses, HttpStatus.OK);

        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(
                    GeneralAPIResponse.builder()
                            .message(ex.getMessage())
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @Override
    public ResponseEntity<?> searchFiles(String searchTerm) {
        try {
            List<FileEntity> files = fileRepository.searchByFileName(searchTerm);

            List<FileResponse> responses = files.stream()
                    .map(file -> convertToFileResponse(file, file.getUploadedBy()))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(responses, HttpStatus.OK);

        } catch (Exception ex) {
            log.error("Error searching files", ex);
            return new ResponseEntity<>(
                    GeneralAPIResponse.builder()
                            .message("Failed to search files")
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public ResponseEntity<?> filterFilesByType(String fileType) {
        try {
            List<FileEntity> files = fileRepository.filterByFileType(fileType);

            List<FileResponse> responses = files.stream()
                    .map(file -> convertToFileResponse(file, file.getUploadedBy()))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(responses, HttpStatus.OK);

        } catch (Exception ex) {
            log.error("Error filtering files", ex);
            return new ResponseEntity<>(
                    GeneralAPIResponse.builder()
                            .message("Failed to filter files")
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public ResponseEntity<?> getUserFiles(String userEmail) {
        try {
            User user = userRepository.findByEmail(userEmail.trim().toLowerCase())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            List<FileEntity> files = fileRepository.findByUploadedBy(user);

            List<FileResponse> responses = files.stream()
                    .filter(FileEntity::getIsActive)
                    .map(file -> convertToFileResponse(file, user))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(responses, HttpStatus.OK);

        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(
                    GeneralAPIResponse.builder()
                            .message(ex.getMessage())
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    // ==================== HELPER METHODS ====================

    private FolderResponse convertToFolderResponse(Folder folder, User currentUser) {
        return FolderResponse.builder()
                .id(folder.getId())
                .name(folder.getName())
                .description(folder.getDescription())
                .folderPath(folder.getFolderPath())
                .parentFolderId(folder.getParentFolder() != null ? folder.getParentFolder().getId() : null)
                .parentFolderName(folder.getParentFolder() != null ? folder.getParentFolder().getName() : null)
                .createdByEmail(folder.getCreatedBy().getEmail())
                .createdByName(folder.getCreatedBy().getFullName())
                .createdAt(folder.getCreatedAt())
                .updatedAt(folder.getUpdatedAt())
                .subFolderCount(folder.getSubFolders() != null ? folder.getSubFolders().size() : 0)
                .fileCount(folder.getFiles() != null ? (int) folder.getFiles().stream().filter(FileEntity::getIsActive).count() : 0)
                .build();
    }

    private FileResponse convertToFileResponse(FileEntity file, User currentUser) {
        boolean canDelete = false;
        if (currentUser.getRole() == Role.ADMIN) {
            canDelete = true;
        } else if (file.getUploadedBy().getId().equals(currentUser.getId())) {
            canDelete = true;
        }

        return FileResponse.builder()
                .id(file.getId())
                .fileName(file.getFileName())
                .fileType(file.getFileType())
                .fileSize(file.getFileSize())
                .fileSizeFormatted(formatFileSize(file.getFileSize()))
                .description(file.getDescription())
                .folderId(file.getFolder().getId())
                .folderName(file.getFolder().getName())
                .folderPath(file.getFolder().getFolderPath())
                .uploadedByEmail(file.getUploadedBy().getEmail())
                .uploadedByName(file.getUploadedBy().getFullName())
                .uploadedAt(file.getUploadedAt())
                .canDelete(canDelete)
                .build();
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        int exp = (int) (Math.log(size) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", size / Math.pow(1024, exp), pre);
    }

    private void deleteAllFilesInFolder(Folder folder) {
        List<FileEntity> files = fileRepository.findAllFilesInFolderAndSubfolders(folder.getFolderPath());
        for (FileEntity file : files) {
            try {
                Path filePath = Paths.get(file.getFilePath());
                Files.deleteIfExists(filePath);
            } catch (IOException ex) {
                log.error("Error deleting physical file: {}", file.getFilePath(), ex);
            }
        }
    }

    private void sendFileUploadNotificationToAdmin(User user, FileEntity file, Folder folder) {
        try {
            // Get all admin users
            List<User> adminUsers = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.ADMIN)
                    .collect(Collectors.toList());

            String subject = "New File Uploaded - " + file.getFileName();
            String message = buildFileUploadEmailContent(user, file, folder);

            for (User admin : adminUsers) {
                try {
                    fileNotificationService.sendFileUploadNotification(admin.getEmail(), subject, message);
                } catch (Exception e) {
                    log.error("Failed to send email to admin: {}", admin.getEmail(), e);
                }
            }

        } catch (Exception ex) {
            log.error("Error sending file upload notification to admin", ex);
        }
    }

    private String buildFileUploadEmailContent(User user, FileEntity file, Folder folder) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("""
                Dear Admin,
                
                A new file has been uploaded to the system.
                
                File Details:
                - File Name: %s
                - File Type: %s
                - File Size: %s
                - Folder: %s
                - Uploaded By: %s (%s)
                - Upload Time: %s
                - Description: %s
                
                Please review the file if necessary.
                
                Regards,
                Security Gateway System
                """,
                file.getFileName(),
                file.getFileType(),
                formatFileSize(file.getFileSize()),
                folder.getFolderPath(),
                user.getFullName(),
                user.getEmail(),
                file.getUploadedAt().format(formatter),
                file.getDescription() != null ? file.getDescription() : "N/A"
        );
    }
}