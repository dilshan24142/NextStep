package com.securitygateway.nextstep.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shuttles")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Shuttle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String busName;
    private String busNumber;
    private String route;
    private String morningStartTime;
    private String eveningDepartureTime;
    private String phoneNumber;

    @Column(length = 1000)
    private String additionalDetails;

    @OneToMany(mappedBy = "shuttle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER) // Use EAGER instead of LAZY
    private List<ShuttleImage> images = new ArrayList<>();
}