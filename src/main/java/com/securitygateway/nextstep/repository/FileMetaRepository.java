package com.securitygateway.nextstep.repository;

import com.securitygateway.nextstep.model.FileMeta;
import com.securitygateway.nextstep.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileMetaRepository extends JpaRepository<FileMeta, Long> {
    List<FileMeta> findByFolder(Folder folder);
    List<FileMeta> findByFileNameContainingIgnoreCase(String keyword);
}
