package com.example.StudentRegistrationSystem.controller;

import com.example.StudentRegistrationSystem.entity.LostItem;
import com.example.StudentRegistrationSystem.service.LostItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lost-found")
public class LostItemController {

    @Autowired
    private LostItemService lostItemService;

    // Report a lost item
    @PostMapping("/report")
    public ResponseEntity<?> reportItem(@Valid @RequestBody LostItem lostItem, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        LostItem savedItem = lostItemService.reportLostItem(lostItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
    }

    // Get all items with pagination
    @GetMapping
    public ResponseEntity<Page<LostItem>> getAllItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Page<LostItem> items = lostItemService.getAllItemsPaginated(page, size, sortBy);
        return ResponseEntity.ok(items);
    }

    // Get item by ID
    @GetMapping("/{id}")
    public ResponseEntity<LostItem> getItemById(@PathVariable Long id) {
        LostItem item = lostItemService.getItemById(id);
        return ResponseEntity.ok(item);
    }

    // Search items
    @GetMapping("/search")
    public ResponseEntity<List<LostItem>> searchItems(@RequestParam String keyword) {
        List<LostItem> items = lostItemService.searchItems(keyword);
        return ResponseEntity.ok(items);
    }

    // Get all unclaimed items with pagination
    @GetMapping("/unclaimed")
    public ResponseEntity<Page<LostItem>> getUnclaimedItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<LostItem> items = lostItemService.getUnclaimedItemsPaginated(page, size);
        return ResponseEntity.ok(items);
    }

    // Claim an item
    @PutMapping("/claim/{id}")
    public ResponseEntity<LostItem> claimItem(@PathVariable Long id) {
        LostItem item = lostItemService.claimItem(id);
        return ResponseEntity.ok(item);
    }

    // Delete an item
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        lostItemService.deleteItem(id);
        return ResponseEntity.ok().build();
    }
}