// src/main/java/com/securitygateway/nextstep/payload/responses/StudyRoomBookingResponse.java
package com.securitygateway.nextstep.payload.responses;

import com.securitygateway.nextstep.model.BookingStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyRoomBookingResponse {

    private Long id;
    private String room;
    private LocalDate date;

    private String startTime; // HH:mm
    private String endTime;   // HH:mm

    private Integer durationMinutes;
    private BookingStatus status;

    // ✅ Admin UI support
    private Long userId;
    private String userEmail;

    // ✅ For UI / expiry display
    private LocalDateTime expireAt;
    private Boolean expired;
}
