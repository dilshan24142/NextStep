package com.securitygateway.nextstep.service.implementation;

import com.securitygateway.nextstep.constants.ApplicationConstants;
import com.securitygateway.nextstep.model.FileMeta;
import com.securitygateway.nextstep.model.Folder;
import com.securitygateway.nextstep.repository.FileMetaRepository;
import com.securitygateway.nextstep.repository.FolderRepository;
import com.securitygateway.nextstep.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {

    private final FolderRepository folderRepository;
    private final FileMetaRepository fileMetaRepository;

    @Override
    public Folder createFolder(String folderName, Long parentId) {
        Folder parent = null;
        if (parentId != null) {
            parent = folderRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Parent folder not found"));
        }
        Optional<Folder> existingFolder = folderRepository.findByNameAndParent(folderName, parent);
        if (existingFolder.isPresent()) return existingFolder.get();

        Folder folder = Folder.builder()
                .name(folderName)
                .parent(parent)
                .build();
        folderRepository.save(folder);

        File dir = new File(ApplicationConstants.UPLOAD_DIR + File.separator + folderName);
        if (!dir.exists()) dir.mkdirs();

        return folder;
    }

    @Override
    public void deleteFolder(Long folderId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        try {
            File dir = new File(ApplicationConstants.UPLOAD_DIR + File.separator + folder.getName());
            if (dir.exists()) {
                Files.walk(dir.toPath())
                        .map(java.nio.file.Path::toFile)
                        .sorted((o1, o2) -> -o1.compareTo(o2))
                        .forEach(File::delete);
            }
        } catch (IOException e) {
            log.error("Error deleting folder", e);
        }

        folderRepository.delete(folder);
    }

    @Override
    public FileMeta uploadFile(MultipartFile file, Long folderId, String uploaderEmail, boolean isAdmin) {

        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        if (isAdmin && !uploaderEmail.equals("defaultadmin@example.com")) {
            throw new RuntimeException("Only the default admin can upload admin files");
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        File uploadRoot = new File(ApplicationConstants.UPLOAD_DIR);
        File folderDir = new File(uploadRoot, folder.getName());

        if (!folderDir.exists()) {
            boolean created = folderDir.mkdirs();
            if (!created) throw new RuntimeException("Could not create upload directory");
        }

        File destination = new File(folderDir, fileName);
        try {
            file.transferTo(destination);
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }

        FileMeta meta = FileMeta.builder()
                .fileName(fileName)
                .filePath(destination.getAbsolutePath())
                .uploadedBy(uploaderEmail)
                .folder(folder)
                .isAdminFile(isAdmin)
                .build();

        return fileMetaRepository.save(meta);
    }

    @Override
    public void deleteFile(Long fileId, String requesterEmail, boolean isAdmin) {
        FileMeta file = fileMetaRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (file.getIsAdminFile() && !requesterEmail.equals("defaultadmin@example.com")) {
            throw new RuntimeException("Only the default admin can delete admin files");
        }

        if (!file.getUploadedBy().equals(requesterEmail) && !requesterEmail.equals("defaultadmin@example.com")) {
            throw new RuntimeException("You can only delete your own files");
        }

        File f = new File(file.getFilePath());
        if (f.exists()) f.delete();

        fileMetaRepository.delete(file);
    }

    @Override
    public List<FileMeta> searchFiles(String keyword) {
        return fileMetaRepository.findByFileNameContainingIgnoreCase(keyword);
    }

    @Override
    public List<FileMeta> listFiles(Long folderId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));
        return fileMetaRepository.findByFolder(folder);
    }
}
