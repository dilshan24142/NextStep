package com.securitygateway.nextstep.controller;

import com.securitygateway.nextstep.model.LostItem;
import com.securitygateway.nextstep.service.LostItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lost-found")
public class LostItemController {

    @Autowired
    private LostItemService lostItemService;


    // GET all items (without pagination)
    @GetMapping
    public ResponseEntity<List<LostItem>> getAllItems() {
        return ResponseEntity.ok(lostItemService.getAllItemsList());
    }

    // GET all items with pagination (for API) - CHANGED METHOD NAME
    @GetMapping("/paginated")
    public ResponseEntity<Page<LostItem>> getAllItemsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // Changed from getAllItemsPaginated to getAllItems
        return ResponseEntity.ok(lostItemService.getAllItems(page, size));
    }

    // POST - Create new lost item
    @PostMapping
    public ResponseEntity<LostItem> createLostItem(@RequestBody LostItem lostItem) {
        LostItem newItem = lostItemService.createItem(lostItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(newItem);
    }

    // GET by ID
    @GetMapping("/{id}")
    public ResponseEntity<LostItem> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(lostItemService.getItemById(id));
    }

    // PUT - Update item
    @PutMapping("/{id}")
    public ResponseEntity<LostItem> updateItem(@PathVariable Long id, @RequestBody LostItem lostItem) {
        return ResponseEntity.ok(lostItemService.updateItem(id, lostItem));
    }

    // DELETE item
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        lostItemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    // GET unclaimed items
    @GetMapping("/unclaimed")
    public ResponseEntity<List<LostItem>> getUnclaimedItems() {
        return ResponseEntity.ok(lostItemService.getUnclaimedItems());
    }

    // Search items
    @GetMapping("/search")
    public ResponseEntity<List<LostItem>> searchItems(@RequestParam String keyword) {
        return ResponseEntity.ok(lostItemService.searchItemsList(keyword));
    }

    // Claim item
    @PutMapping("/{id}/claim")
    public ResponseEntity<LostItem> claimItem(@PathVariable Long id) {
        return ResponseEntity.ok(lostItemService.claimItem(id));
    }
}