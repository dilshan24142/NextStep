// src/main/java/com/securitygateway/nextstep/payload/requests/StudyRoomBookingRequest.java
package com.securitygateway.nextstep.Dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudyRoomBookingRequest {

    @NotBlank
    private String room;

    @NotNull
    private String date; // yyyy-MM-dd

    @NotBlank
    private String time; // HH:mm

    // optional (if null => default 60)
    private Integer durationMinutes;
}