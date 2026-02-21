package com.securitygateway.nextstep.service;

import com.securitygateway.nextstep.Dtos.requests.StudyRoomBookingRequest;
import com.securitygateway.nextstep.Dtos.responses.StudyRoomBookingResponse;
import com.securitygateway.nextstep.model.*;
import com.securitygateway.nextstep.repository.StudyRoomBookingRepository;
import com.securitygateway.nextstep.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyRoomService {

    private final StudyRoomBookingRepository bookingRepository;
    private final UserRepository userRepository;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    // ================= USER =================

    public ResponseEntity<?> bookRoom(StudyRoomBookingRequest req, User user) {
        LocalDate date = LocalDate.parse(req.getDate());
        LocalTime start = LocalTime.parse(req.getTime(), TIME_FMT);
        int duration = req.getDurationMinutes();
        LocalTime end = start.plusMinutes(duration);

        if (bookingRepository.hasOverlap(req.getRoom(), date, start, end))
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Time conflict");

        StudyRoomBooking booking = StudyRoomBooking.builder()
                .room(req.getRoom())
                .date(date)
                .startTime(start)
                .endTime(end)
                .durationMinutes(duration)
                .status(BookingStatus.ACTIVE)
                .user(user)
                .expireAt(LocalDateTime.of(date, end))
                .build();

        bookingRepository.save(booking);

        // Return success message with booking ID
        String responseMessage = "Booked successfully. Booking ID: " + booking.getId();
        return ResponseEntity.ok(responseMessage);
    }
    public ResponseEntity<?> myBookings(User user) {
        List<StudyRoomBookingResponse> list = bookingRepository.findByUserIdOrderByDateDescStartTimeDesc(user.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    public ResponseEntity<?> userViewAllBookings(User user) {
        // For availability chart, show all bookings but read-only
        List<StudyRoomBookingResponse> list = bookingRepository.findAllByOrderByDateDescStartTimeDesc()
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    public ResponseEntity<?> updateBooking(Long id, StudyRoomBookingRequest req, User user) {
        StudyRoomBooking booking = bookingRepository.findByIdAndUserId(id, user.getId()).orElse(null);
        if (booking == null) return ResponseEntity.status(404).body("Booking not found");

        LocalDateTime bookingStart = LocalDateTime.of(booking.getDate(), booking.getStartTime());
        if (LocalDateTime.now().isAfter(bookingStart.minusHours(2)))
            return ResponseEntity.badRequest().body("Must update 2 hours before");

        LocalDate date = LocalDate.parse(req.getDate());
        LocalTime start = LocalTime.parse(req.getTime(), TIME_FMT);
        int duration = req.getDurationMinutes();
        LocalTime end = start.plusMinutes(duration);

        if (bookingRepository.hasOverlapExcludingId(id, req.getRoom(), date, start, end))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Time conflict");

        booking.setRoom(req.getRoom());
        booking.setDate(date);
        booking.setStartTime(start);
        booking.setEndTime(end);
        booking.setDurationMinutes(duration);
        booking.setExpireAt(LocalDateTime.of(date, end));

        bookingRepository.save(booking);
        return ResponseEntity.ok("Updated successfully");
    }

    public ResponseEntity<?> cancelBooking(Long id, User user) {
        StudyRoomBooking booking = bookingRepository.findByIdAndUserId(id, user.getId()).orElse(null);
        if (booking == null) {
            return ResponseEntity.status(404).body("Booking with ID " + id + " not found");
        }

        bookingRepository.delete(booking);

        // Return a response including the cancelled booking ID
        return ResponseEntity.ok("Booking with ID " + id + " cancelled successfully");
    }

    public ResponseEntity<?> availability(String dateStr, Integer durationMinutes) {

        LocalDate date = LocalDate.parse(dateStr);

        LocalTime openTime = LocalTime.of(8, 0);
        LocalTime closeTime = LocalTime.of(22, 0);

        List<String> allRooms = List.of("A1", "A2", "A3", "B1", "B2", "C1", "C2", "C3");

        List<StudyRoomBooking> bookings =
                bookingRepository.findByDateAndStatusOrderByRoomAscStartTimeAsc(
                        date, BookingStatus.ACTIVE);

        Map<String, List<StudyRoomBooking>> bookingsByRoom =
                bookings.stream().collect(Collectors.groupingBy(StudyRoomBooking::getRoom));

        List<Map<String, String>> availableDetails = new ArrayList<>();

        for (String room : allRooms) {

            List<StudyRoomBooking> roomBookings =
                    bookingsByRoom.getOrDefault(room, new ArrayList<>());

            if (roomBookings.isEmpty()) {

                availableDetails.add(Map.of(
                        "room", room,
                        "availableFrom", openTime.toString(),
                        "availableTo", closeTime.toString()
                ));

            } else {

                LocalTime previousEnd = openTime;

                for (StudyRoomBooking booking : roomBookings) {

                    if (previousEnd.isBefore(booking.getStartTime())) {

                        availableDetails.add(Map.of(
                                "room", room,
                                "availableFrom", previousEnd.toString(),
                                "availableTo", booking.getStartTime().toString()
                        ));
                    }

                    previousEnd = booking.getEndTime();
                }

                if (previousEnd.isBefore(closeTime)) {

                    availableDetails.add(Map.of(
                            "room", room,
                            "availableFrom", previousEnd.toString(),
                            "availableTo", closeTime.toString()
                    ));
                }
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("bookedDetails", bookings);
        response.put("availableDetails", availableDetails);

        return ResponseEntity.ok(response);
    }

    // ================= ADMIN =================

    public ResponseEntity<?> adminAllBookings(User admin) {
        List<StudyRoomBooking> bookings = bookingRepository.findAllByOrderByDateDescStartTimeDesc();
        Map<Long, Map<String, Object>> userBookingsMap = new LinkedHashMap<>();

        for (StudyRoomBooking b : bookings) {
            Long userId = b.getUser().getId();
            userBookingsMap.putIfAbsent(userId, Map.of(
                    "userId", userId,
                    "userName", b.getUser().getFullName(),
                    "userEmail", b.getUser().getEmail(),
                    "bookings", new ArrayList<>()
            ));

            List<Map<String, Object>> userBookings = (List<Map<String, Object>>) userBookingsMap.get(userId).get("bookings");
            userBookings.add(Map.of(
                    "id", b.getId(),
                    "room", b.getRoom(),
                    "date", b.getDate().toString(),
                    "startTime", b.getStartTime().format(TIME_FMT),
                    "endTime", b.getEndTime().format(TIME_FMT),
                    "durationMinutes", b.getDurationMinutes(),
                    "status", b.getStatus()
            ));
        }

        return ResponseEntity.ok(userBookingsMap.values());
    }

    public ResponseEntity<?> adminDeleteBooking(Long id, User admin) {
        // 1️⃣ Booking එක query කරන්න
        Optional<StudyRoomBooking> optionalBooking = bookingRepository.findById(id);

        if (optionalBooking.isEmpty()) {
            return ResponseEntity.status(404).body("Booking not found");
        }

        StudyRoomBooking booking = optionalBooking.get();

        // 2️⃣ Booking delete කරන්න
        bookingRepository.deleteById(id);

        // 3️⃣ Response prepare කරන්න
        Map<String, Object> response = Map.of(
                "deletedBookingId", booking.getId(),
                "userId", booking.getUser().getId(),
                "userName", booking.getUser().getFullName(),
                "userEmail", booking.getUser().getEmail()
        );

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> adminUpdateAny(Long id, StudyRoomBookingRequest req, User admin) {
        StudyRoomBooking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) return ResponseEntity.status(404).body("Booking not found");

        LocalDate date = LocalDate.parse(req.getDate());
        LocalTime start = LocalTime.parse(req.getTime(), TIME_FMT);
        int duration = req.getDurationMinutes();
        LocalTime end = start.plusMinutes(duration);

        if (bookingRepository.hasOverlapExcludingId(id, req.getRoom(), date, start, end))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Time conflict");

        booking.setRoom(req.getRoom());
        booking.setDate(date);
        booking.setStartTime(start);
        booking.setEndTime(end);
        booking.setDurationMinutes(duration);
        booking.setExpireAt(LocalDateTime.of(date, end));

        bookingRepository.save(booking);

        // Return updated booking details
        Map<String, Object> response = Map.of(
                "bookingId", booking.getId(),
                "userId", booking.getUser().getId(),
                "userName", booking.getUser().getFullName(),
                "userEmail", booking.getUser().getEmail(),
                "room", booking.getRoom(),
                "date", booking.getDate().toString(),
                "startTime", booking.getStartTime().format(TIME_FMT),
                "endTime", booking.getEndTime().format(TIME_FMT),
                "durationMinutes", booking.getDurationMinutes()
        );

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> adminBookForUser(Long userId, StudyRoomBookingRequest req, User admin) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.status(404).body("User not found");

        return bookRoom(req, user);
    }

    public ResponseEntity<?> adminViewAllUsersBookings(User admin) {
        List<User> users = userRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (User u : users) {
            // Fetch bookings for this user
            List<StudyRoomBooking> bookings = bookingRepository.findAllByUserOrderByDateDescStartTimeDesc(u);

            // Map each booking to a clean JSON structure
            List<Map<String, Object>> userBookings = bookings.stream()
                    .map(b -> Map.<String, Object>of(
                            "bookingId", b.getId(),
                            "room", b.getRoom(),
                            "date", b.getDate().toString(),
                            "startTime", b.getStartTime().format(TIME_FMT),
                            "endTime", b.getEndTime().format(TIME_FMT),
                            "durationMinutes", b.getDurationMinutes(),
                            "status", b.getStatus().name() // better as String
                    ))
                    .collect(Collectors.toList());

            // Create user map with bookings
            Map<String, Object> userMap = Map.of(
                    "userId", u.getId(),
                    "userName", u.getFullName(),
                    "userEmail", u.getEmail(),
                    "bookings", userBookings
            );

            result.add(userMap);
        }

        return ResponseEntity.ok(result);
    }

    // ================= UTIL =================
    private StudyRoomBookingResponse toResponse(StudyRoomBooking b) {
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
                .userName(b.getUser().getFullName())
                .expireAt(b.getExpireAt())
                .expired(LocalDateTime.now().isAfter(b.getExpireAt()))
                .build();
    }
}