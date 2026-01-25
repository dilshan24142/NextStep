package com.securitygateway.nextstep.repository;

import com.securitygateway.nextstep.model.StudyRoomBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyRoomBookingRepository extends JpaRepository<StudyRoomBooking, Long> {
    List<StudyRoomBooking> findByUserIdOrderByDateDesc(Long userId);
}
