package com.securitygateway.nextstep.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shuttle_images")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ShuttleImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "image_data", columnDefinition = "LONGBLOB") // LONGBLOB is mandatory
    private byte[] imageData;

    private String fileType;

    @ManyToOne(fetch = FetchType.LAZY) // Use LAZY to improve performance
    @JoinColumn(name = "shuttle_id")
    @JsonIgnore
    private Shuttle shuttle;
}