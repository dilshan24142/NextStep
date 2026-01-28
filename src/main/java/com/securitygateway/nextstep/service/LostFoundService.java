package com.securitygateway.nextstep.service;


import com.securitygateway.nextstep.payload.requests.LostFoundCommentRequest;
import com.securitygateway.nextstep.payload.requests.LostFoundItemRequest;
import com.securitygateway.nextstep.payload.responses.LostFoundCommentResponse;
import com.securitygateway.nextstep.payload.responses.LostFoundItemResponse;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LostFoundService {

    LostFoundItemResponse createItem(LostFoundItemRequest request, MultipartFile image, String email);
    LostFoundItemResponse updateItem(Long id, LostFoundItemRequest request, MultipartFile image, String email);
    void deleteItem(Long id, String email);
    List<LostFoundItemResponse> getAllItems();
    LostFoundItemResponse getItemById(Long id);
    LostFoundCommentResponse addComment(Long itemId, LostFoundCommentRequest request, String email);
    void updateItemStatusToReturned(Long id, String email);
    List<LostFoundItemResponse> getItemsPaginated(Pageable pageable);


}
