// src/main/java/com/securitygateway/nextstep/payload/requests/StudyRoomBookingRequest.java
package com.securitygateway.nextstep.payload.requests;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class StudyRoomBookingRequest {

    @NotBlank(message = "Room is required")
    private String room;

    @NotBlank(message = "Date is required")
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "Date must be in format yyyy-MM-dd"
    )
    private String date; // yyyy-MM-dd

    @NotBlank(message = "Time is required")
    @Pattern(
            regexp = "^([01]\\d|2[0-3]):[0-5]\\d$",
            message = "Time must be in format HH:mm (24-hour)"
    )
    private String time; // HH:mm

    @Min(value = 1, message = "Minimum duration is 1 minute")
    @Max(value = 240, message = "Maximum duration is 240 minutes")
    private Integer durationMinutes; // optional (null => default 60)
}
