package com.securitygateway.nextstep.payload.responses;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LostFoundCommentResponse1 {

    private Long id;
    private String commentText;
    private String commentedBy;
    private LocalDateTime commentedAt;
}
