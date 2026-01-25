package com.securitygateway.nextstep.controller;

import com.securitygateway.nextstep.model.User;
import com.securitygateway.nextstep.payload.requests.StudyRoomBookingRequest;
import com.securitygateway.nextstep.service.StudyRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/study-room")
public class StudyRoomController {

    private final StudyRoomService studyRoomService;

    @PostMapping("/book")
    public ResponseEntity<?> bookRoom(
            @Valid @RequestBody StudyRoomBookingRequest request,
            @AuthenticationPrincipal User user
    ) {
        return studyRoomService.bookRoom(request, user);
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<?> myBookings(@AuthenticationPrincipal User user) {
        return studyRoomService.myBookings(user);
    }
}
