package com.securitygateway.nextstep.controller;

import com.securitygateway.nextstep.payload.requests.LostFoundCommentRequest;
import com.securitygateway.nextstep.payload.requests.LostFoundItemRequest;
import com.securitygateway.nextstep.payload.responses.LostFoundCommentResponse;
import com.securitygateway.nextstep.payload.responses.LostFoundItemResponse;
import com.securitygateway.nextstep.service.LostFoundService;
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
public class LostFoundController {

    private final LostFoundService lostFoundService;

    // CREATE ITEM with image upload
    @PostMapping(consumes = "multipart/form-data")
    public LostFoundItemResponse create(@ModelAttribute @Valid LostFoundItemRequest request,
                                        @RequestParam("image") MultipartFile image,
                                        Authentication authentication) {
        return lostFoundService.createItem(request, image, authentication.getName());
    }

    // UPDATE ITEM with optional image upload
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public LostFoundItemResponse update(@PathVariable Long id,
                                        @ModelAttribute @Valid LostFoundItemRequest request,
                                        @RequestParam(value = "image", required = false) MultipartFile image,
                                        Authentication authentication) {
        return lostFoundService.updateItem(id, request, image, authentication.getName());
    }

    // DELETE ITEM
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Authentication authentication) {
        lostFoundService.deleteItem(id, authentication.getName());
    }

    // GET ALL ITEMS
    @GetMapping
    public List<LostFoundItemResponse> getAll() {
        return lostFoundService.getAllItems();
    }

    // GET ONE ITEM
    @GetMapping("/{id}")
    public LostFoundItemResponse getOne(@PathVariable Long id) {
        return lostFoundService.getItemById(id);
    }

    // ADD COMMENT
    @PostMapping("/{id}/comments")
    public LostFoundCommentResponse comment(@PathVariable Long id,
                                            @Valid @RequestBody LostFoundCommentRequest request,
                                            Authentication authentication) {
        return lostFoundService.addComment(id, request, authentication.getName());
    }

    // MARK ITEM AS RETURNED
    @PutMapping("/{id}/mark-returned")
    public ResponseEntity<?> markAsReturned(@PathVariable Long id, Authentication authentication) {
        lostFoundService.updateItemStatusToReturned(id, authentication.getName());
        return ResponseEntity.ok("Item marked as returned");
    }

    @GetMapping("/paginated")
    public List<LostFoundItemResponse> getPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return lostFoundService.getItemsPaginated(pageable);
    }
}
