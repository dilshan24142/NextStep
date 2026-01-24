package com.securitygateway.nextstep.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "files")
public class FileMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String filePath;

    private String uploadedBy; // email of uploader

    @ManyToOne
    private Folder folder;

    private Boolean isAdminFile; // true if admin uploaded
}
