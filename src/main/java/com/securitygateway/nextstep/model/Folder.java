package com.securitygateway.nextstep.model;



import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "folders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String campus;     // e.g., Colombo
    private String year;       // e.g., 2024
    private String faculty;    // e.g., Computing
    private String module;     // e.g., Module 1
    private String semester;   // e.g., Semester 1
}
