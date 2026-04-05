package com.securitygateway.nextstep.repository;

import com.securitygateway.nextstep.model.StallBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StallBookingRepository extends JpaRepository<StallBooking, Long> {
    List<StallBooking> findByBookedBy(String bookedBy);
}
