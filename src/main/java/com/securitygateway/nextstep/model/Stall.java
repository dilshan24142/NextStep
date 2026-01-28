package com.securitygateway.nextstep.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "stalls")
public class Stall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(length = 500)
    private String description;

    private String location;

    private Integer capacity;

    @Column(name = "price_per_hour")
    private BigDecimal pricePerHour;

    @Column(name = "available_from")
    private String availableFrom;

    @Column(name = "available_to")
    private String availableTo;

    // ✅ FIXED: Change field name and add proper getters/setters
    @Column(name = "is_available")
    private Boolean isAvailable = true; // Changed from "available" to "isAvailable"

    // Constructors
    public Stall() {}

    public Stall(String name, String category, String description) {
        this.name = name;
        this.category = category;
        this.description = description;
    }

    // Getters and Setters (முழுவதும்)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public BigDecimal getPricePerHour() { return pricePerHour; }
    public void setPricePerHour(BigDecimal pricePerHour) { this.pricePerHour = pricePerHour; }

    public String getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(String availableFrom) { this.availableFrom = availableFrom; }

    public String getAvailableTo() { return availableTo; }
    public void setAvailableTo(String availableTo) { this.availableTo = availableTo; }

    // ✅ FIXED: Proper getter and setter for isAvailable
    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

    // Alternative getter for Thymeleaf compatibility
    public Boolean isAvailable() { return isAvailable; }
    public void setAvailable(Boolean available) { this.isAvailable = available; }

    @Override
    public String toString() {
        return "Stall{id=" + id + ", name='" + name + "', category='" + category + "'}";
    }
}