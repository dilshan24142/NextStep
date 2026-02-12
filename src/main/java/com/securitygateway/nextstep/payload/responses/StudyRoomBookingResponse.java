
package com.securitygateway.nextstep.payload.responses;

import com.securitygateway.nextstep.model.BookingStatus;
import lombok.*;

import java.time.LocalDate;

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

    // ✅ for admin UI
    private Long userId;
}
