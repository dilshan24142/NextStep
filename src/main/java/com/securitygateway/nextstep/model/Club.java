package com.securitygateway.nextstep.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clubName;
    private String description;

    // ✅ Cascade delete events
    @OneToMany(mappedBy = "club",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Event> events;

    // ✅ ADD THIS (VERY IMPORTANT)
    @OneToMany(mappedBy = "club",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ClubJoinRequest> joinRequests;
}