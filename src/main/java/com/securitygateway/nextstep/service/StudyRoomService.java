package com.securitygateway.nextstep.service;

import com.securitygateway.nextstep.model.StudyRoomBooking;
import com.securitygateway.nextstep.model.User;
import com.securitygateway.nextstep.payload.requests.StudyRoomBookingRequest;
import com.securitygateway.nextstep.payload.responses.GeneralAPIResponse;
import com.securitygateway.nextstep.payload.responses.StudyRoomBookingResponse;
import com.securitygateway.nextstep.repository.StudyRoomBookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyRoomService {

    private final StudyRoomBookingRepository bookingRepository;

    public ResponseEntity<?> bookRoom(StudyRoomBookingRequest request, User user) {
        if (user == null || user.getId() == null) {
            return ResponseEntity.status(401).body(
                    GeneralAPIResponse.builder().message("Unauthorized: Please login again").build()
            );
        }

        StudyRoomBooking booking = StudyRoomBooking.builder()
                .room(request.getRoom())
                .date(LocalDate.parse(request.getDate()))
                .time(request.getTime())
                .user(user)
                .build();

        bookingRepository.save(booking);

        return ResponseEntity.ok(
                GeneralAPIResponse.builder().message("Room booked successfully").build()
        );
    }

    public ResponseEntity<?> myBookings(User user) {
        if (user == null || user.getId() == null) {
            return ResponseEntity.status(401).body(
                    GeneralAPIResponse.builder().message("Unauthorized: Please login again").build()
            );
        }

        List<StudyRoomBooking> bookings = bookingRepository.findByUserIdOrderByDateDesc(user.getId());

        List<StudyRoomBookingResponse> response = bookings.stream()
                .map(b -> StudyRoomBookingResponse.builder()
                        .id(b.getId())
                        .room(b.getRoom())
                        .date(b.getDate())
                        .time(b.getTime())
                        .build())
                .toList();

        return ResponseEntity.ok(response);
    }
}
