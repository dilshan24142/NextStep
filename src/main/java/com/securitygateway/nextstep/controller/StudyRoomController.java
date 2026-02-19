package com.securitygateway.nextstep.controller;

import com.securitygateway.nextstep.model.User;
import com.securitygateway.nextstep.Dtos.requests.StudyRoomBookingRequest;
import com.securitygateway.nextstep.service.StudyRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/study-room")
@RequiredArgsConstructor
@CrossOrigin
public class StudyRoomController {

    private final StudyRoomService studyRoomService;

    // ================= USER =================

    @PostMapping("/book")
    public ResponseEntity<?> book(@Valid @RequestBody StudyRoomBookingRequest req,
                                  @AuthenticationPrincipal User user) {
        return studyRoomService.bookRoom(req, user);
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<?> myBookings(@AuthenticationPrincipal User user) {
        return studyRoomService.myBookings(user);
    }

    // USER can view all bookings (read only)
    @GetMapping("/all-bookings")
    public ResponseEntity<?> allBookings(@AuthenticationPrincipal User user) {
        return studyRoomService.userViewAllBookings(user);
    }

    @PutMapping("/bookings/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable Long id,
                                           @Valid @RequestBody StudyRoomBookingRequest req,
                                           @AuthenticationPrincipal User user) {
        return studyRoomService.updateBooking(id, req, user);
    }

    @PatchMapping("/bookings/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id,
                                    @AuthenticationPrincipal User user) {
        return studyRoomService.cancelBooking(id, user);
    }

    @GetMapping("/availability")
    public ResponseEntity<?> availability(@RequestParam String date,
                                          @RequestParam String time,
                                          @RequestParam(required = false) Integer durationMinutes) {
        return studyRoomService.availability(date, time, durationMinutes);
    }

    // ================= ADMIN =================

    @GetMapping("/admin/bookings/all")
    public ResponseEntity<?> adminAll(@AuthenticationPrincipal User user) {
        return studyRoomService.adminAllBookings(user);
    }

    @DeleteMapping("/admin/bookings/{id}")
    public ResponseEntity<?> adminDelete(@PathVariable Long id,
                                         @AuthenticationPrincipal User user) {
        return studyRoomService.adminDeleteBooking(id, user);
    }

    @PutMapping("/admin/bookings/{id}")
    public ResponseEntity<?> adminUpdate(@PathVariable Long id,
                                         @Valid @RequestBody StudyRoomBookingRequest req,
                                         @AuthenticationPrincipal User user) {
        return studyRoomService.adminUpdateAny(id, req, user);
    }

    @PostMapping("/admin/book-for-user/{userId}")
    public ResponseEntity<?> adminBookForUser(@PathVariable Long userId,
                                              @Valid @RequestBody StudyRoomBookingRequest req,
                                              @AuthenticationPrincipal User user) {
        return studyRoomService.adminBookForUser(userId, req, user);
    }
}
