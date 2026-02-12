package com.securitygateway.nextstep.service.implementation;

import com.securitygateway.nextstep.model.*;
import com.securitygateway.nextstep.payload.requests.LostFoundCommentRequest1;
import com.securitygateway.nextstep.payload.requests.LostFoundItemRequest1;
import com.securitygateway.nextstep.payload.responses.LostFoundCommentResponse1;
import com.securitygateway.nextstep.payload.responses.LostFoundItemResponse1;
import com.securitygateway.nextstep.repository.*;
import com.securitygateway.nextstep.service.FileStorageService;
import com.securitygateway.nextstep.service.LostFoundService1;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LostFoundService1Implementation1 implements LostFoundService1 {

    private final LostFoundRepository1 itemRepository;
    private final LostFoundCommentRepository1 commentRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;


    // ================= CREATE ITEM =================
    @Override
    public LostFoundItemResponse1 createItem(LostFoundItemRequest1 request, MultipartFile image, String email) {
        User user = userRepository.findByEmail(email).orElseThrow();

        String imagePath = fileStorageService.storeFile(image);

        LostFoundItem1 item = LostFoundItem1.builder()
                .itemName(request.getItemName())
                .lectureHall(request.getLectureHall())
                .description(request.getDescription())
                .leftAt(request.getLeftAt())
                .imagePath(imagePath)
                .status(ItemStatus.LOST)
                .createdBy(user)
                .build();

        return mapToResponse(itemRepository.save(item));
    }


    // ================= UPDATE ITEM (ADMIN OR OWNER) =================
    @Override
    public LostFoundItemResponse1 updateItem(Long id, LostFoundItemRequest1 request, MultipartFile image, String email) {
        LostFoundItem1 item = itemRepository.findById(id).orElseThrow();
        User user = userRepository.findByEmail(email).orElseThrow();

        boolean isAdmin = user.getRole().name().equals("ADMIN");
        boolean isOwner = item.getCreatedBy().getEmail().equals(email);

        if (!isAdmin && !isOwner) {
            throw new RuntimeException("You are not allowed");
        }

        item.setItemName(request.getItemName());
        item.setLectureHall(request.getLectureHall());
        item.setDescription(request.getDescription());
        item.setLeftAt(request.getLeftAt());

        if (image != null && !image.isEmpty()) {
            String imagePath = fileStorageService.storeFile(image);
            item.setImagePath(imagePath);
        }

        return mapToResponse(itemRepository.save(item));
    }


    // ================= DELETE ITEM (ADMIN OR OWNER) =================
    @Override
    public void deleteItem(Long id, String email) {
        LostFoundItem1 item = itemRepository.findById(id).orElseThrow();
        User user = userRepository.findByEmail(email).orElseThrow();

        boolean isAdmin = user.getRole().name().equals("ADMIN");
        boolean isOwner = item.getCreatedBy().getEmail().equals(email);

        if (!isAdmin && !isOwner) {
            throw new RuntimeException("You are not allowed to delete this post");
        }

        itemRepository.delete(item);
    }

    // ================= GET ALL =================
    @Override
    public List<LostFoundItemResponse1> getAllItems() {
        return itemRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ================= GET BY ID =================
    @Override
    public LostFoundItemResponse1 getItemById(Long id) {
        return mapToResponse(itemRepository.findById(id).orElseThrow());
    }

    // ================= ADD COMMENT =================
    @Override
    public LostFoundCommentResponse1 addComment(Long itemId, LostFoundCommentRequest1 request, String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        LostFoundItem1 item = itemRepository.findById(itemId).orElseThrow();

        LostFoundComment1 comment = LostFoundComment1.builder()
                .commentText(request.getCommentText())
                .commentedBy(user)
                .lostFoundItem1(item)
                .commentedAt(LocalDateTime.now())
                .build();

        return mapComment(commentRepository.save(comment));
    }

    // ================= MAPPERS =================
    private LostFoundItemResponse1 mapToResponse(LostFoundItem1 item) {
        return LostFoundItemResponse1.builder()
                .id(item.getId())
                .itemName(item.getItemName())
                .lectureHall(item.getLectureHall())
                .description(item.getDescription())
                .pictureUrl(item.getImagePath()) // updated to imagePath
                .leftAt(item.getLeftAt())
                .postedBy(item.getCreatedBy().getFullName())
                .comments(item.getComments().stream().map(this::mapComment).collect(Collectors.toList()))
                .status(item.getStatus())
                .build();
    }


    private LostFoundCommentResponse1 mapComment(LostFoundComment1 comment) {
        return LostFoundCommentResponse1.builder()
                .id(comment.getId())
                .commentText(comment.getCommentText())
                .commentedBy(comment.getCommentedBy().getFullName())
                .commentedAt(comment.getCommentedAt())
                .build();
    }
    @Override
    public void updateItemStatusToReturned(Long id, String email) {
        LostFoundItem1 item = itemRepository.findById(id).orElseThrow();
        User user = userRepository.findByEmail(email).orElseThrow();

        boolean isAdmin = user.getRole().name().equals("ADMIN");
        boolean isOwner = item.getCreatedBy().getEmail().equals(email);

        if (!isAdmin && !isOwner) {
            throw new RuntimeException("You are not allowed to mark this item as returned");
        }

        item.setStatus(ItemStatus.RETURNED);
        itemRepository.save(item);
    }
    @Override
    public List<LostFoundItemResponse1> getItemsPaginated(Pageable pageable) {
        return itemRepository.findAll(pageable)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


}
