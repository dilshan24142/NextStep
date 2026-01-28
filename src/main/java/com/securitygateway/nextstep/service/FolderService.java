package com.securitygateway.nextstep.service;


import com.securitygateway.nextstep.model.Folder;
import com.securitygateway.nextstep.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;

    // Admin creates folder categories
    public Folder createFolder(Folder folder) {
        return folderRepository.save(folder);
    }

    // Everyone can view folders (for filter dropdowns)
    public List<Folder> getAllFolders() {
        return folderRepository.findAll();
    }
}
