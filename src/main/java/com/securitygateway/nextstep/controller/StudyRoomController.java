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
@RequestMapping("/api/v1/study-room")
@RequiredArgsConstructor
@CrossOrigin
public class StudyRoomController {

    private final StudyRoomService studyRoomService;

    // =========================
    // USER
    // =========================

    @PostMapping("/book")
    public ResponseEntity<?> book(@Valid @RequestBody StudyRoomBookingRequest req,
                                  @AuthenticationPrincipal User user) {
        return studyRoomService.bookRoom(req, user);
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<?> myBookings(@AuthenticationPrincipal User user) {
        return studyRoomService.myBookings(user);
    }

    @GetMapping("/availability")
    public ResponseEntity<?> availability(@RequestParam String date,
                                          @RequestParam String time,
                                          @RequestParam(required = false) Integer durationMinutes) {
        return studyRoomService.availability(date, time, durationMinutes);
    }

    // ✅ NEW FEATURE: Update booking (owner only)
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

    // =========================
    // ADMIN
    // =========================

    @GetMapping("/admin/bookings/all")
    public ResponseEntity<?> adminAll(@AuthenticationPrincipal User user) {
        return studyRoomService.adminAllBookings(user);
    }

    @GetMapping("/admin/bookings/by-date")
    public ResponseEntity<?> adminByDate(@RequestParam String date,
                                         @AuthenticationPrincipal User user) {
        return studyRoomService.adminBookingsByDate(date, user);
    }

    @GetMapping("/admin/bookings/by-status")
    public ResponseEntity<?> adminByStatus(@RequestParam String status,
                                           @AuthenticationPrincipal User user) {
        return studyRoomService.adminBookingsByStatus(status, user);
    }

    @DeleteMapping("/admin/bookings/{id}")
    public ResponseEntity<?> adminDelete(@PathVariable Long id,
                                         @AuthenticationPrincipal User user) {
        return studyRoomService.adminDeleteBooking(id, user);
    }
}
