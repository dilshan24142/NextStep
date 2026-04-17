package com.securitygateway.nextstep.Dtos.responses;

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

    // Admin & User display
    private Long userId;
    private String userEmail;
    private String userName;   // ✅ ADDED

    // Expiry display
    private LocalDateTime expireAt;
    private Boolean expired;
}
