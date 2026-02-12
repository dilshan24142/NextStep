package com.securitygateway.nextstep.repository;

import com.securitygateway.nextstep.model.Folder;
import com.securitygateway.nextstep.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {

    // Find folders by parent folder
    List<Folder> findByParentFolder(Folder parentFolder);

    // Find root folders (no parent)
    List<Folder> findByParentFolderIsNull();

    // Find folder by name and parent
    Optional<Folder> findByNameAndParentFolder(String name, Folder parentFolder);

    // Find folders created by user
    List<Folder> findByCreatedBy(User user);

    // Search folders by name
    @Query("SELECT f FROM Folder f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Folder> searchByName(@Param("name") String name);

    // Find folder by path
    Optional<Folder> findByFolderPath(String folderPath);

    // Check if folder name exists under parent
    boolean existsByNameAndParentFolder(String name, Folder parentFolder);
}