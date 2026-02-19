package com.securitygateway.nextstep.controller;

import com.securitygateway.nextstep.model.Stall;
import com.securitygateway.nextstep.model.StallBooking;
import com.securitygateway.nextstep.Dtos.requests.StallBookingRequest;
import com.securitygateway.nextstep.repository.StallBookingRepository;
import com.securitygateway.nextstep.repository.StallRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final StallBookingRepository bookingRepository;
    private final StallRepository stallRepository;

    public BookingController(StallBookingRepository bookingRepository, StallRepository stallRepository) {
        this.bookingRepository = bookingRepository;
        this.stallRepository = stallRepository;
    }

    // Create a booking
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody StallBookingRequest request) {

        Stall stall = stallRepository.findById(request.getStallId()).orElse(null);
        if (stall == null) {
            return ResponseEntity.badRequest().body("Invalid stall ID");
        }

        StallBooking booking = new StallBooking();
        booking.setStall(stall);
        booking.setBookedBy(request.getBookedBy());
        booking.setDate(request.getDate());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setTotalPrice(request.getTotalPrice());
        booking.setStatus("CONFIRMED");

        bookingRepository.save(booking);
        return ResponseEntity.ok(booking);
    }

    // Get all bookings
    @GetMapping
    public List<StallBooking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // Get bookings by email
    @GetMapping("/user/{email}")
    public List<StallBooking> getBookingsByEmail(@PathVariable String email) {
        return bookingRepository.findByBookedBy(email);
    }

    // Delete booking
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        if (!bookingRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("Booking not found");
        }
        bookingRepository.deleteById(id);
        return ResponseEntity.ok("Booking deleted");
    }
}
