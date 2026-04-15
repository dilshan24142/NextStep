package com.securitygateway.nextstep.repository;

import com.securitygateway.nextstep.model.FileEntity;
import com.securitygateway.nextstep.model.Folder;
import com.securitygateway.nextstep.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

    // Find files in a folder
    List<FileEntity> findByFolder(Folder folder);

    // Find files uploaded by user
    List<FileEntity> findByUploadedBy(User user);

    // Find active files in folder
    List<FileEntity> findByFolderAndIsActiveTrue(Folder folder);

    // Search files by name
    @Query("SELECT f FROM FileEntity f WHERE LOWER(f.fileName) LIKE LOWER(CONCAT('%', :fileName, '%')) AND f.isActive = true")
    List<FileEntity> searchByFileName(@Param("fileName") String fileName);

    // Filter files by type
    @Query("SELECT f FROM FileEntity f WHERE f.fileType = :fileType AND f.isActive = true")
    List<FileEntity> filterByFileType(@Param("fileType") String fileType);

    // Find file by name in folder
    Optional<FileEntity> findByFileNameAndFolder(String fileName, Folder folder);

    // Count files by user
    Long countByUploadedBy(User user);

    // Get all files in folder and subfolders
    @Query("SELECT f FROM FileEntity f WHERE f.folder.folderPath LIKE CONCAT(:folderPath, '%') AND f.isActive = true")
    List<FileEntity> findAllFilesInFolderAndSubfolders(@Param("folderPath") String folderPath);
}