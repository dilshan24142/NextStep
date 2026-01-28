package com.securitygateway.nextstep.payload.requests;

import com.securitygateway.nextstep.model.ItemStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LostFoundItemRequest {

    @NotBlank
    private String itemName;

    @NotBlank
    private String lectureHall;

    private String description;



    private ItemStatus status; // optional when admin updates


    @NotNull
    private LocalDateTime leftAt;
}
