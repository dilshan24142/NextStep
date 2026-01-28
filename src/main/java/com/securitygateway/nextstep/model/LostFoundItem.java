package com.securitygateway.nextstep.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "lost_found_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LostFoundItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;
    private String lectureHall;
    private String description;

    // store uploaded image file path
    private String imagePath;

    private LocalDateTime leftAt;

    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "lostFoundItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LostFoundComment> comments;
}
