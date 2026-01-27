package com.securitygateway.nextstep.service;

import com.securitygateway.nextstep.model.FileMeta;
import com.securitygateway.nextstep.model.Folder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    Folder createFolder(String folderName, Long parentId);

    void deleteFolder(Long folderId);

    FileMeta uploadFile(MultipartFile file, Long folderId, String uploaderEmail, boolean isAdmin);

    void deleteFile(Long fileId, String requesterEmail, boolean isAdmin);

    List<FileMeta> searchFiles(String keyword);

    List<FileMeta> listFiles(Long folderId);
}
