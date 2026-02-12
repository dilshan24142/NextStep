package com.securitygateway.nextstep.service;


import com.securitygateway.nextstep.payload.requests.LostFoundCommentRequest1;
import com.securitygateway.nextstep.payload.requests.LostFoundItemRequest1;
import com.securitygateway.nextstep.payload.responses.LostFoundCommentResponse1;
import com.securitygateway.nextstep.payload.responses.LostFoundItemResponse1;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LostFoundService1 {

    LostFoundItemResponse1 createItem(LostFoundItemRequest1 request, MultipartFile image, String email);
    LostFoundItemResponse1 updateItem(Long id, LostFoundItemRequest1 request, MultipartFile image, String email);
    void deleteItem(Long id, String email);
    List<LostFoundItemResponse1> getAllItems();
    LostFoundItemResponse1 getItemById(Long id);
    LostFoundCommentResponse1 addComment(Long itemId, LostFoundCommentRequest1 request, String email);
    void updateItemStatusToReturned(Long id, String email);
    List<LostFoundItemResponse1> getItemsPaginated(Pageable pageable);


}
