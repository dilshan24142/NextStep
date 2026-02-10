package com.example.StudentRegistrationSystem.repository;

import com.example.StudentRegistrationSystem.entity.LostItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LostItemRepository extends JpaRepository<LostItem, Long> {
    long countByClaimedFalse();

    Page<LostItem> findByItemNameContainingOrDescriptionContaining(
            String itemName, String description, Pageable pageable);

    // Basic search methods
    List<LostItem> findByItemNameContaining(String keyword);
    List<LostItem> findByLocationFound(String location);
    List<LostItem> findByClaimedFalse();  // CHANGED HERE

    // Pagination methods
    Page<LostItem> findAll(Pageable pageable);
    Page<LostItem> findByClaimedFalse(Pageable pageable);  // CHANGED HERE
    Page<LostItem> findByItemNameContaining(String keyword, Pageable pageable);
}