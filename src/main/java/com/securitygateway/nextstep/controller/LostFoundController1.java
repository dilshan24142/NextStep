package com.securitygateway.nextstep.controller;

import com.securitygateway.nextstep.payload.requests.LostFoundCommentRequest1;
import com.securitygateway.nextstep.payload.requests.LostFoundItemRequest1;
import com.securitygateway.nextstep.payload.responses.LostFoundCommentResponse1;
import com.securitygateway.nextstep.payload.responses.LostFoundItemResponse1;
import com.securitygateway.nextstep.service.LostFoundService1;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lostfound")
@RequiredArgsConstructor
public class LostFoundController1 {

    private final LostFoundService1 lostFoundService1;

    // CREATE ITEM with image upload
    @PostMapping(consumes = "multipart/form-data")
    public LostFoundItemResponse1 create(@ModelAttribute @Valid LostFoundItemRequest1 request,
                                         @RequestParam("image") MultipartFile image,
                                         Authentication authentication) {
        return lostFoundService1.createItem(request, image, authentication.getName());
    }

    // UPDATE ITEM with optional image upload
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public LostFoundItemResponse1 update(@PathVariable Long id,
                                         @ModelAttribute @Valid LostFoundItemRequest1 request,
                                         @RequestParam(value = "image", required = false) MultipartFile image,
                                         Authentication authentication) {
        return lostFoundService1.updateItem(id, request, image, authentication.getName());
    }

    // DELETE ITEM
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Authentication authentication) {
        lostFoundService1.deleteItem(id, authentication.getName());
    }

    // GET ALL ITEMS
    @GetMapping
    public List<LostFoundItemResponse1> getAll() {
        return lostFoundService1.getAllItems();
    }

    // GET ONE ITEM
    @GetMapping("/{id}")
    public LostFoundItemResponse1 getOne(@PathVariable Long id) {
        return lostFoundService1.getItemById(id);
    }

    // ADD COMMENT
    @PostMapping("/{id}/comments")
    public LostFoundCommentResponse1 comment(@PathVariable Long id,
                                             @Valid @RequestBody LostFoundCommentRequest1 request,
                                             Authentication authentication) {
        return lostFoundService1.addComment(id, request, authentication.getName());
    }

    // MARK ITEM AS RETURNED
    @PutMapping("/{id}/mark-returned")
    public ResponseEntity<?> markAsReturned(@PathVariable Long id, Authentication authentication) {
        lostFoundService1.updateItemStatusToReturned(id, authentication.getName());
        return ResponseEntity.ok("Item marked as returned");
    }

    @GetMapping("/paginated")
    public List<LostFoundItemResponse1> getPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return lostFoundService1.getItemsPaginated(pageable);
    }
}
