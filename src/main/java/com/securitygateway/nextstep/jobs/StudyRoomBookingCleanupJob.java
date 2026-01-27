package com.securitygateway.nextstep.jobs;

import com.securitygateway.nextstep.model.BookingStatus;
import com.securitygateway.nextstep.repository.StudyRoomBookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class StudyRoomBookingCleanupJob {

    private final StudyRoomBookingRepository bookingRepository;

    @Scheduled(fixedRate = 5 * 60 * 1000) // every 5 minutes
    @Transactional
    public void deleteCancelledOldBookings() {
        bookingRepository.deleteExpired(LocalDateTime.now() );
    }
}
