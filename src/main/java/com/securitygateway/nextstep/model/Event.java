package com.securitygateway.nextstep.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Event {

    public enum Status { PENDING, APPROVED, REJECTED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private LocalDateTime eventDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String createdBy; // student email

    @ManyToOne
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;
}
