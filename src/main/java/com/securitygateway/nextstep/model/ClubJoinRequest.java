package com.securitygateway.nextstep.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ClubJoinRequest {

    public enum Status { PENDING, APPROVED, DECLINED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentEmail;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @Enumerated(EnumType.STRING)
    private Status status;
}
