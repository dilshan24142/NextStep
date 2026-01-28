package com.securitygateway.nextstep.payload.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LostFoundCommentRequest {

    @NotBlank
    private String commentText;
}
