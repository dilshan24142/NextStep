package com.securitygateway.nextstep.service;

import com.securitygateway.nextstep.model.*;
import com.securitygateway.nextstep.payload.requests.StudyRoomBookingRequest;
import com.securitygateway.nextstep.payload.responses.GeneralAPIResponse;
import com.securitygateway.nextstep.payload.responses.StudyRoomBookingResponse;
import com.securitygateway.nextstep.repository.StudyRoomBookingRepository;
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

    private static final List<String> ROOMS = List.of("A1","A2","A3","B1","B2","C1","C2","C3");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private static final LocalTime OPEN_TIME = LocalTime.of(8, 0);
    private static final LocalTime CLOSE_TIME = LocalTime.of(21, 0);

    // ✅ expiry status update
    private void runExpiryNow() {
        bookingRepository.markExpired(LocalDateTime.now());
    }

    private boolean isAdmin(User user) {
        if (user == null) return false;
        Object role = user.getRole();
        return role != null && role.toString().equalsIgnoreCase("ADMIN");
    }

    private ResponseEntity<?> unauthorized() {
        return ResponseEntity.status(401).body(
                GeneralAPIResponse.builder().message("Unauthorized: Please login again").build()
        );
    }

    private ResponseEntity<?> forbidden() {
        return ResponseEntity.status(403).body(
                GeneralAPIResponse.builder().message("Forbidden: Admin only").build()
        );
    }

    private StudyRoomBookingResponse toRes(StudyRoomBooking b) {
        return StudyRoomBookingResponse.builder()
                .id(b.getId())
                .room(b.getRoom())
                .date(b.getDate())
                .startTime(b.getStartTime() == null ? null : b.getStartTime().format(TIME_FMT))
                .endTime(b.getEndTime() == null ? null : b.getEndTime().format(TIME_FMT))
                .durationMinutes(b.getDurationMinutes())
                .status(b.getStatus())
                .userId(b.getUser() != null ? b.getUser().getId() : null)
                .build();
    }

    private ResponseEntity<?> validateTimeWindow(LocalTime start, LocalTime end) {
        if (start.isBefore(OPEN_TIME)) {
            return ResponseEntity.badRequest().body(
                    GeneralAPIResponse.builder().message("Bookings open at 08:00 AM").build()
            );
        }
        if (end.isAfter(CLOSE_TIME)) {
            return ResponseEntity.badRequest().body(
                    GeneralAPIResponse.builder().message("Bookings must end by 09:00 PM").build()
            );
        }
        return null;
    }

    // ------------------ CREATE ------------------
    @Transactional
    public ResponseEntity<?> bookRoom(StudyRoomBookingRequest request, User user) {
        if (user == null || user.getId() == null) return unauthorized();
        runExpiryNow();

        String room = request.getRoom() == null ? "" : request.getRoom().trim();
        if (!ROOMS.contains(room)) {
            return ResponseEntity.badRequest().body(
                    GeneralAPIResponse.builder().message("Invalid room. Allowed: " + ROOMS).build()
            );
        }

        LocalDate date;
        try {
            date = LocalDate.parse(request.getDate().trim());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    GeneralAPIResponse.builder().message("Invalid date. Use yyyy-MM-dd").build()
            );
        }

        LocalTime start;
        try {
            start = LocalTime.parse(request.getTime().trim(), TIME_FMT);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    GeneralAPIResponse.builder().message("Invalid time. Use HH:mm (e.g. 14:30)").build()
            );
        }

        int duration = (request.getDurationMinutes() == null) ? 60 : request.getDurationMinutes();
        if (duration <= 0 || duration > 240) {
            return ResponseEntity.badRequest().body(
                    GeneralAPIResponse.builder().message("durationMinutes must be 1..240").build()
            );
        }

        LocalTime end = start.plusMinutes(duration);

        ResponseEntity<?> timeErr = validateTimeWindow(start, end);
        if (timeErr != null) return timeErr;

        if (bookingRepository.hasOverlap(room, date, start, end)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    GeneralAPIResponse.builder().message("Time overlap: already booked").build()
            );
        }

        LocalDateTime expireAt = LocalDateTime.of(date, end);

        StudyRoomBooking booking = StudyRoomBooking.builder()
                .room(room)
                .date(date)
                .startTime(start)
                .endTime(end)
                .durationMinutes(duration)
                .expireAt(expireAt)
                .status(BookingStatus.ACTIVE)
                .user(user)
                .build();

        StudyRoomBooking saved = bookingRepository.save(booking);

        Map<String, Object> res = new HashMap<>();
        res.put("message", "Room booked successfully");
        res.put("booking", toRes(saved));
        res.put("totalRooms", ROOMS.size());
        return ResponseEntity.ok(res);
    }

    // ------------------ READ: MY BOOKINGS ------------------
    @Transactional
    public ResponseEntity<?> myBookings(User user) {
        if (user == null || user.getId() == null) return unauthorized();
        runExpiryNow();

        List<StudyRoomBookingResponse> list = bookingRepository
                .findByUserIdOrderByDateDescStartTimeDesc(user.getId())
                .stream().map(this::toRes).toList();

        return ResponseEntity.ok(list);
    }

    // ------------------ AVAILABILITY ------------------
    @Transactional
    public ResponseEntity<?> availability(String dateStr, String timeStr, Integer durationMinutes) {
        runExpiryNow();

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr.trim());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    GeneralAPIResponse.builder().message("Invalid date. Use yyyy-MM-dd").build()
            );
        }

        LocalTime start;
        try {
            start = LocalTime.parse(timeStr.trim(), TIME_FMT);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    GeneralAPIResponse.builder().message("Invalid time. Use HH:mm").build()
            );
        }

        int duration = (durationMinutes == null) ? 60 : durationMinutes;
        if (duration <= 0 || duration > 240) {
            return ResponseEntity.badRequest().body(
                    GeneralAPIResponse.builder().message("durationMinutes must be 1..240").build()
            );
        }

        LocalTime end = start.plusMinutes(duration);

        ResponseEntity<?> timeErr = validateTimeWindow(start, end);
        if (timeErr != null) return timeErr;

        List<String> availableRooms = ROOMS.stream()
                .filter(r -> !bookingRepository.hasOverlap(r, date, start, end))
                .toList();

        Map<String, Object> res = new HashMap<>();
        res.put("date", dateStr);
        res.put("startTime", timeStr);
        res.put("endTime", end.format(TIME_FMT));
        res.put("durationMinutes", duration);
        res.put("totalRooms", ROOMS.size());
        res.put("availableRoomsCount", availableRooms.size());
        res.put("availableRooms", availableRooms);

        return ResponseEntity.ok(res);
    }

    // ✅ NEW FEATURE: UPDATE (OWNER ONLY)
    @Transactional
    public ResponseEntity<?> updateBooking(Long id, StudyRoomBookingRequest request, User user) {
        if (user == null || user.getId() == null) return unauthorized();
        runExpiryNow();

        StudyRoomBooking booking = bookingRepository.findByIdAndUserId(id, user.getId()).orElse(null);
        if (booking == null) {
            return ResponseEntity.status(404).body(
                    GeneralAPIResponse.builder().message("Booking not found").build()
            );
        }

        if (booking.getStatus() != BookingStatus.ACTIVE) {
            return ResponseEntity.badRequest().body(
                    GeneralAPIResponse.builder().message("Only ACTIVE bookings can be updated").build()
            );
        }

        String room = request.getRoom() == null ? "" : request.getRoom().trim();
        if (!ROOMS.contains(room)) {
            return ResponseEntity.badRequest().body(
                    GeneralAPIResponse.builder().message("Invalid room. Allowed: " + ROOMS).build()
            );
        }

        LocalDate date;
        try {
            date = LocalDate.parse(request.getDate().trim());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    GeneralAPIResponse.builder().message("Invalid date. Use yyyy-MM-dd").build()
            );
        }

        LocalTime start;
        try {
            start = LocalTime.parse(request.getTime().trim(), TIME_FMT);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    GeneralAPIResponse.builder().message("Invalid time. Use HH:mm").build()
            );
        }

        int duration = (request.getDurationMinutes() == null) ? 60 : request.getDurationMinutes();
        if (duration <= 0 || duration > 240) {
            return ResponseEntity.badRequest().body(
                    GeneralAPIResponse.builder().message("durationMinutes must be 1..240").build()
            );
        }

        LocalTime end = start.plusMinutes(duration);

        ResponseEntity<?> timeErr = validateTimeWindow(start, end);
        if (timeErr != null) return timeErr;

        // ✅ exclude current booking id when checking overlap
        if (bookingRepository.hasOverlapExcludingId(booking.getId(), room, date, start, end)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    GeneralAPIResponse.builder().message("Time overlap: already booked").build()
            );
        }

        booking.setRoom(room);
        booking.setDate(date);
        booking.setStartTime(start);
        booking.setEndTime(end);
        booking.setDurationMinutes(duration);
        booking.setExpireAt(LocalDateTime.of(date, end));

        bookingRepository.save(booking);

        Map<String, Object> res = new HashMap<>();
        res.put("message", "Booking updated successfully");
        res.put("booking", toRes(booking));
        return ResponseEntity.ok(res);
    }

    // ------------------ CANCEL (OWNER ONLY) ------------------
    @Transactional
    public ResponseEntity<?> cancelBooking(Long id, User user) {
        if (user == null || user.getId() == null) return unauthorized();
        runExpiryNow();

        StudyRoomBooking booking = bookingRepository.findByIdAndUserId(id, user.getId()).orElse(null);
        if (booking == null) {
            return ResponseEntity.status(404).body(
                    GeneralAPIResponse.builder().message("Booking not found").build()
            );
        }

        if (booking.getStatus() != BookingStatus.ACTIVE) {
            return ResponseEntity.badRequest().body(
                    GeneralAPIResponse.builder().message("Booking already " + booking.getStatus()).build()
            );
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        return ResponseEntity.ok(
                GeneralAPIResponse.builder().message("Booking cancelled successfully").build()
        );
    }

    // ------------------ ADMIN: ALL BOOKINGS ------------------
    @Transactional
    public ResponseEntity<?> adminAllBookings(User user) {
        if (user == null || user.getId() == null) return unauthorized();
        if (!isAdmin(user)) return forbidden();
        runExpiryNow();

        List<StudyRoomBookingResponse> list = bookingRepository
                .findAllByOrderByDateDescStartTimeDesc()
                .stream().map(this::toRes).toList();

        Map<String, Object> res = new HashMap<>();
        res.put("totalRooms", ROOMS.size());
        res.put("bookings", list);
        return ResponseEntity.ok(res);
    }

    // ------------------ ADMIN: BY DATE (ACTIVE ONLY) ------------------
    @Transactional
    public ResponseEntity<?> adminBookingsByDate(String dateStr, User user) {
        if (user == null || user.getId() == null) return unauthorized();
        if (!isAdmin(user)) return forbidden();
        runExpiryNow();

        LocalDate date = LocalDate.parse(dateStr.trim());

        List<StudyRoomBookingResponse> list = bookingRepository
                .findByDateAndStatusOrderByRoomAscStartTimeAsc(date, BookingStatus.ACTIVE)
                .stream().map(this::toRes).toList();

        return ResponseEntity.ok(list);
    }

    // ------------------ ADMIN: BY STATUS ------------------
    @Transactional
    public ResponseEntity<?> adminBookingsByStatus(String statusStr, User user) {
        if (user == null || user.getId() == null) return unauthorized();
        if (!isAdmin(user)) return forbidden();
        runExpiryNow();

        BookingStatus status;
        try {
            status = BookingStatus.valueOf(statusStr.trim().toUpperCase());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    GeneralAPIResponse.builder().message("Invalid status. Use ACTIVE/CANCELLED/EXPIRED").build()
            );
        }

        List<StudyRoomBookingResponse> list = bookingRepository
                .findByStatusOrderByDateDescStartTimeDesc(status)
                .stream().map(this::toRes).toList();

        return ResponseEntity.ok(list);
    }

    // ------------------ ADMIN: HARD DELETE ------------------
    @Transactional
    public ResponseEntity<?> adminDeleteBooking(Long id, User user) {
        if (user == null || user.getId() == null) return unauthorized();
        if (!isAdmin(user)) return forbidden();

        if (!bookingRepository.existsById(id)) {
            return ResponseEntity.status(404).body(
                    GeneralAPIResponse.builder().message("Booking not found").build()
            );
        }

        bookingRepository.deleteById(id);
        return ResponseEntity.ok(
                GeneralAPIResponse.builder().message("Booking permanently deleted").build()
        );
    }
}
