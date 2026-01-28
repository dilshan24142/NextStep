package com.securitygateway.nextstep.repository;


import com.securitygateway.nextstep.model.FileDocument;
import com.securitygateway.nextstep.model.FileStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileDocumentRepository extends JpaRepository<FileDocument, Long> {

    List<FileDocument> findByStatus(FileStatus status);

    List<FileDocument> findByFolderIdAndStatus(Long folderId, FileStatus status);
    List<FileDocument> findByFolder_CampusAndFolder_YearAndFolder_FacultyAndFolder_ModuleAndFolder_SemesterAndStatus(
            String campus,
            String year,
            String faculty,
            String module,
            String semester,
            FileStatus status
    );
}

