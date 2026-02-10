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

    private LocalDateTime dateFound;  // Correct field name

    @Email(message = "Please provide valid email")
    @NotBlank(message = "Email is required")
    private String contactEmail;

    private boolean claimed;  // Changed from "isClaimed" to "claimed"

    private String category;
    private String imagePath;

    // Constructors
    public LostItem() {}

    public LostItem(String itemName, String description, String locationFound, String contactEmail) {
        this.itemName = itemName;
        this.description = description;
        this.locationFound = locationFound;
        this.contactEmail = contactEmail;
        this.dateFound = LocalDateTime.now();
        this.claimed = false;
        this.category = "General";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocationFound() {
        return locationFound;
    }

    public void setLocationFound(String locationFound) {
        this.locationFound = locationFound;
    }

    public LocalDateTime getDateFound() {
        return dateFound;
    }

    public void setDateFound(LocalDateTime dateFound) {
        this.dateFound = dateFound;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public void setClaimed(boolean claimed) {
        this.claimed = claimed;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}