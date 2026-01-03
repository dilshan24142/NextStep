package com.example.StudentRegistrationSystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lost_items")
public class LostItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Item name is required")
    @Size(min = 2, max = 100, message = "Item name must be 2-100 characters")
    private String itemName;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotBlank(message = "Location is required")
    private String locationFound;

    private LocalDateTime dateFound;

    @Email(message = "Please provide valid email")
    @NotBlank(message = "Email is required")
    private String contactEmail;

    private boolean isClaimed;

    private String category;
    private String imagePath;

    // Constructors, getters, setters
}