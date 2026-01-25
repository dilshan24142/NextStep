package com.securitygateway.nextstep.payload.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyRoomBookingResponse {
    private Long id;
    private String room;
    private LocalDate date;
    private String time;
}
