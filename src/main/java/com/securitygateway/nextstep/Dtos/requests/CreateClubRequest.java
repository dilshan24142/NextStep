package com.securitygateway.nextstep.Dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateClubRequest {
    @NotBlank
    private String clubName;

    @NotBlank
    private String description;
}
