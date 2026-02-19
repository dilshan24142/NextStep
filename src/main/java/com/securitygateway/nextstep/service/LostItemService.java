package com.securitygateway.nextstep.service;

import com.securitygateway.nextstep.model.LostItem;
import com.securitygateway.nextstep.repository.LostItemRepository;
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

    // 1. CREATE - Report lost item
    public LostItem createItem(LostItem lostItem) {
        lostItem.setDateFound(LocalDateTime.now());
        lostItem.setClaimed(false);
        return lostItemRepository.save(lostItem);
    }

    // 2. READ - Get item by ID
    public LostItem getItemById(Long id) {
        return lostItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
    }

    // 3. READ - Get all items WITH PAGINATION (for Web Controller)
    public Page<LostItem> getAllItems(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateFound").descending());
        return lostItemRepository.findAll(pageable);
    }

    // 4. READ - Get all items WITHOUT PAGINATION (for other use)
    public List<LostItem> getAllItemsList() {
        return lostItemRepository.findAll();
    }

    // 5. SEARCH - With pagination (for Web Controller)
    public Page<LostItem> searchItems(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateFound").descending());
        return lostItemRepository.findByItemNameContainingOrDescriptionContaining(
                keyword, keyword, pageable);
    }

    // 6. SEARCH - Without pagination
    public List<LostItem> searchItemsList(String keyword) {
        return lostItemRepository.findByItemNameContaining(keyword);
    }

    // 7. Get unclaimed items without pagination
    public List<LostItem> getUnclaimedItems() {
        return lostItemRepository.findByClaimedFalse();
    }

    // 8. Get unclaimed items with pagination
    public Page<LostItem> getUnclaimedItemsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateFound").descending());
        return lostItemRepository.findByClaimedFalse(pageable);
    }

    // 9. UPDATE - Claim item
    public LostItem claimItem(Long id) {
        LostItem item = getItemById(id);
        item.setClaimed(true);
        return lostItemRepository.save(item);
    }

    // 10. UPDATE - General update
    public LostItem updateItem(Long id, LostItem itemDetails) {
        LostItem item = getItemById(id);
        item.setItemName(itemDetails.getItemName());
        item.setDescription(itemDetails.getDescription());
        item.setLocationFound(itemDetails.getLocationFound());
        item.setContactEmail(itemDetails.getContactEmail());
        item.setCategory(itemDetails.getCategory());
        return lostItemRepository.save(item);
    }

    // 11. DELETE
    public void deleteItem(Long id) {
        lostItemRepository.deleteById(id);
    }

    // 12. COUNT - Total items
    public long countItems() {
        return lostItemRepository.count();
    }

    // 13. COUNT - Unclaimed items
    public long countUnclaimedItems() {
        return lostItemRepository.countByClaimedFalse();
    }
    // LostItemService.java-ல் கடைசியில் add பண்ணுங்க:
    public LostItem reportLostItem(LostItem lostItem) {
        return createItem(lostItem);
    }
}