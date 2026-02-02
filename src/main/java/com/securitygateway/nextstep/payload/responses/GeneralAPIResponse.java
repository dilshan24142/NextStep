package com.securitygateway.nextstep.payload.responses;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class GeneralAPIResponse {
    private String message;
}
