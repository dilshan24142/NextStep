package com.securitygateway.nextstep.Dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClubJoinRequestDTO {
    @NotNull
    private Long clubId;

    @NotNull
    @Email
    private String email;
}
