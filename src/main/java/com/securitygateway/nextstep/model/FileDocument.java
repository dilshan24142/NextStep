package com.securitygateway.nextstep.model;



import com.securitygateway.nextstep.model.User; // use your existing User entity
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;          // Display name
    private String fileName;       // Stored file name
    private String filePath;       // Server path

    @Enumerated(EnumType.STRING)
    private FileStatus status;     // APPROVED / PENDING / REJECTED

    private LocalDateTime uploadDate;

    // Who uploaded
    @ManyToOne
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    // Which folder/category it belongs to
    @ManyToOne
    @JoinColumn(name = "folder_id")
    private Folder folder;
}
