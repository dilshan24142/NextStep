package com.securitygateway.nextstep.service;

import com.securitygateway.nextstep.model.StallBooking;
import com.securitygateway.nextstep.repository.StallBookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StallBookingService {

    private final StallBookingRepository bookingRepository;

    public StallBookingService(StallBookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<StallBooking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public StallBooking getBookingById(Long id) {
        return bookingRepository.findById(id).orElse(null);
    }

    public StallBooking saveBooking(StallBooking booking) {
        return bookingRepository.save(booking);
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}
