package com.securitygateway.nextstep.repository;

import com.securitygateway.nextstep.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    Optional<Folder> findByNameAndParent(String name, Folder parent);
}
