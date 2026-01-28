package com.securitygateway.nextstep.payload.responses;

import com.securitygateway.nextstep.model.ItemStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class LostFoundItemResponse {

    private Long id;
    private String itemName;
    private String lectureHall;
    private String description;
    private String pictureUrl;
    private LocalDateTime leftAt;
    private String postedBy;
    private List<LostFoundCommentResponse> comments;
    private ItemStatus status;

}