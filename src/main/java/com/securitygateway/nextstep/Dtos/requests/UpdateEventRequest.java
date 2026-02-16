package com.securitygateway.nextstep.Dtos.requests;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateEventRequest {
    @NotBlank private String title;
    @NotBlank private String description;
    @Future private LocalDateTime eventDate;
}
