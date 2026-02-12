package com.securitygateway.nextstep.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LostFoundComment1 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String commentText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User commentedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lost_found_item_id")
    private LostFoundItem1 lostFoundItem1;

    @Column(nullable = false)
    private LocalDateTime commentedAt;
}
