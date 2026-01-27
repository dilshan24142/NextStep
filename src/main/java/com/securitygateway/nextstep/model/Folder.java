package com.securitygateway.nextstep.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "folders")
// Cyclic reference (එකම දේ කැරකී කැරකී පෙන්වීම) වැළැක්වීමට මෙය එකතු කරන්න
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "parent"})
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonInclude(JsonInclude.Include.NON_NULL) // Parent null නම් JSON එකේ පෙන්වන්න එපා
    private Folder parent;

    // @Builder පාවිච්චි කරන නිසා default values වැඩ කරන්න මෙහෙම දෙන්න ඕනේ 👇
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Folder> subFolders = new ArrayList<>();

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL)
    @Builder.Default
    private List<FileMeta> files = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}