// src/main/java/com/securitygateway/nextstep/controller/StudyRoomController.java
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
    // USER ENDPOINTS
    // =========================

    @PostMapping("/book")
    public ResponseEntity<?> book(@Valid @RequestBody StudyRoomBookingRequest req,
                                  @AuthenticationPrincipal User user) {
        // Regular user books for self
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
    // ADMIN ENDPOINTS
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

    @PatchMapping("/admin/bookings/{id}/cancel")
    public ResponseEntity<?> adminCancelAny(@PathVariable Long id,
                                            @AuthenticationPrincipal User user) {
        return studyRoomService.adminCancelAny(id, user);
    }

    @PutMapping("/admin/bookings/{id}")
    public ResponseEntity<?> adminUpdateAny(@PathVariable Long id,
                                            @Valid @RequestBody StudyRoomBookingRequest req,
                                            @AuthenticationPrincipal User user) {
        return studyRoomService.adminUpdateAny(id, req, user);
    }

    // Admin books for any user (userId in path)
    @PostMapping("/admin/book-for-user/{userId}")
    public ResponseEntity<?> adminBookForUser(@PathVariable Long userId,
                                              @Valid @RequestBody StudyRoomBookingRequest req,
                                              @AuthenticationPrincipal User user) {
        return studyRoomService.adminBookForUser(userId, req, user);
    }

    // Admin list of all users (for sidebar)
    @GetMapping("/admin/users")
    public ResponseEntity<?> adminUsers(@AuthenticationPrincipal User user) {
        return studyRoomService.adminUsers(user);
    }

    // Admin stats for all users (for charts)
    @GetMapping("/admin/users/stats")
    public ResponseEntity<?> adminUserStats(@AuthenticationPrincipal User user) {
        return studyRoomService.adminUserStats(user);
    }
}
