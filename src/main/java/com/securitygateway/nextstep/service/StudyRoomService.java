package com.securitygateway.nextstep.service;

import com.securitygateway.nextstep.model.*;
import com.securitygateway.nextstep.Dtos.requests.StudyRoomBookingRequest;
import com.securitygateway.nextstep.Dtos.responses.GeneralAPIResponse;
import com.securitygateway.nextstep.Dtos.responses.StudyRoomBookingResponse;
import com.securitygateway.nextstep.repository.StudyRoomBookingRepository;
import com.securitygateway.nextstep.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StudyRoomService {

    private final StudyRoomBookingRepository bookingRepository;
    private final UserRepository userRepository;

    private static final List<String> ROOMS =
            List.of("A1","A2","A3","B1","B2","C1","C2","C3");

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm");

    private boolean isAdmin(User user) {
        return user.getRole().toString().equalsIgnoreCase("ADMIN");
    }

    private StudyRoomBookingResponse toRes(StudyRoomBooking b) {
        return StudyRoomBookingResponse.builder()
                .id(b.getId())
                .room(b.getRoom())
                .date(b.getDate())
                .startTime(b.getStartTime().format(TIME_FMT))
                .endTime(b.getEndTime().format(TIME_FMT))
                .durationMinutes(b.getDurationMinutes())
                .status(b.getStatus())
                .userId(b.getUser().getId())
                .userEmail(b.getUser().getEmail())
                .expireAt(b.getExpireAt())
                .expired(b.getStatus() == BookingStatus.EXPIRED)
                .build();
    }

    // ================= USER =================

    @Transactional
    public ResponseEntity<?> bookRoom(StudyRoomBookingRequest req, User user) {

        LocalDate date = LocalDate.parse(req.getDate());
        LocalTime start = LocalTime.parse(req.getTime(), TIME_FMT);
        int duration = req.getDurationMinutes() == null ? 60 : req.getDurationMinutes();
        LocalTime end = start.plusMinutes(duration);

        if (!ROOMS.contains(req.getRoom()))
            return ResponseEntity.badRequest().body("Invalid room");

        if (bookingRepository.hasOverlap(req.getRoom(), date, start, end))
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Time already booked");

        StudyRoomBooking booking = StudyRoomBooking.builder()
                .room(req.getRoom())
                .date(date)
                .startTime(start)
                .endTime(end)
                .durationMinutes(duration)
                .expireAt(LocalDateTime.of(date, end))
                .status(BookingStatus.ACTIVE)
                .user(user)
                .build();

        bookingRepository.save(booking);

        return ResponseEntity.ok("Booked successfully");
    }

    @Transactional
    public ResponseEntity<?> myBookings(User user) {
        return ResponseEntity.ok(
                bookingRepository.findByUserIdOrderByDateDescStartTimeDesc(user.getId())
                        .stream().map(this::toRes).toList()
        );
    }

    // ✅ USER VIEW ALL (read only)
    @Transactional
    public ResponseEntity<?> userViewAllBookings(User user) {
        return ResponseEntity.ok(
                bookingRepository.findAllByOrderByDateDescStartTimeDesc()
                        .stream().map(this::toRes).toList()
        );
    }

    @Transactional
    public ResponseEntity<?> cancelBooking(Long id, User user) {

        StudyRoomBooking booking =
                bookingRepository.findByIdAndUserId(id, user.getId())
                        .orElse(null);

        if (booking == null)
            return ResponseEntity.status(404).body("Booking not found");

        if (booking.getStatus() != BookingStatus.ACTIVE)
            return ResponseEntity.badRequest().body("Already cancelled");

        // ✅ 2-hour rule
        LocalDateTime bookingStart =
                LocalDateTime.of(booking.getDate(), booking.getStartTime());

        if (LocalDateTime.now().isAfter(bookingStart.minusHours(2)))
            return ResponseEntity.badRequest()
                    .body("Must cancel 2 hours before");

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        return ResponseEntity.ok("Cancelled successfully");
    }

    @Transactional
    public ResponseEntity<?> updateBooking(Long id,
                                           StudyRoomBookingRequest req,
                                           User user) {

        StudyRoomBooking booking =
                bookingRepository.findByIdAndUserId(id, user.getId())
                        .orElse(null);

        if (booking == null)
            return ResponseEntity.status(404).body("Booking not found");

        LocalDate date = LocalDate.parse(req.getDate());
        LocalTime start = LocalTime.parse(req.getTime(), TIME_FMT);
        int duration = req.getDurationMinutes();
        LocalTime end = start.plusMinutes(duration);

        if (bookingRepository.hasOverlapExcludingId(
                id, req.getRoom(), date, start, end))
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Time conflict");

        booking.setRoom(req.getRoom());
        booking.setDate(date);
        booking.setStartTime(start);
        booking.setEndTime(end);
        booking.setDurationMinutes(duration);

        bookingRepository.save(booking);

        return ResponseEntity.ok("Updated successfully");
    }

    // ================= ADMIN =================

    @Transactional
    public ResponseEntity<?> adminAllBookings(User user) {

        if (!isAdmin(user))
            return ResponseEntity.status(403).body("Forbidden");

        return ResponseEntity.ok(
                bookingRepository.findAllByOrderByDateDescStartTimeDesc()
                        .stream().map(this::toRes).toList()
        );
    }

    @Transactional
    public ResponseEntity<?> adminDeleteBooking(Long id, User user) {

        if (!isAdmin(user))
            return ResponseEntity.status(403).body("Forbidden");

        bookingRepository.deleteById(id);
        return ResponseEntity.ok("Deleted permanently");
    }

    @Transactional
    public ResponseEntity<?> adminUpdateAny(Long id,
                                            StudyRoomBookingRequest req,
                                            User user) {

        if (!isAdmin(user))
            return ResponseEntity.status(403).body("Forbidden");

        StudyRoomBooking booking =
                bookingRepository.findById(id).orElse(null);

        if (booking == null)
            return ResponseEntity.status(404).body("Not found");

        LocalDate date = LocalDate.parse(req.getDate());
        LocalTime start = LocalTime.parse(req.getTime(), TIME_FMT);
        int duration = req.getDurationMinutes();
        LocalTime end = start.plusMinutes(duration);

        booking.setRoom(req.getRoom());
        booking.setDate(date);
        booking.setStartTime(start);
        booking.setEndTime(end);
        booking.setDurationMinutes(duration);

        bookingRepository.save(booking);

        return ResponseEntity.ok("Admin updated booking");
    }

    @Transactional
    public ResponseEntity<?> adminBookForUser(Long userId,
                                              StudyRoomBookingRequest req,
                                              User admin) {

        if (!isAdmin(admin))
            return ResponseEntity.status(403).body("Forbidden");

        User target = userRepository.findById(userId)
                .orElseThrow();

        return bookRoom(req, target);
    }

    // ================= AVAILABILITY =================

    @Transactional
    public ResponseEntity<?> availability(String dateStr,
                                          String timeStr,
                                          Integer duration) {

        LocalDate date = LocalDate.parse(dateStr);
        LocalTime start = LocalTime.parse(timeStr, TIME_FMT);
        int dur = duration == null ? 60 : duration;
        LocalTime end = start.plusMinutes(dur);

        List<String> available = ROOMS.stream()
                .filter(r -> !bookingRepository
                        .hasOverlap(r, date, start, end))
                .toList();

        return ResponseEntity.ok(Map.of(
                "availableRooms", available,
                "totalRooms", ROOMS.size()
        ));
    }
}
