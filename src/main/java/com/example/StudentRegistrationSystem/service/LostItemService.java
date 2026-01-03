package com.example.StudentRegistrationSystem.service;

import com.example.StudentRegistrationSystem.entity.LostItem;
import com.example.StudentRegistrationSystem.repository.LostItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LostItemService {

    @Autowired
    private LostItemRepository lostItemRepository;

    // Report lost item
    public LostItem reportLostItem(LostItem lostItem) {
        lostItem.setDateFound(LocalDateTime.now());
        lostItem.setClaimed(false);
        return lostItemRepository.save(lostItem);
    }

    // Get item by ID
    public LostItem getItemById(Long id) {
        return lostItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
    }
}