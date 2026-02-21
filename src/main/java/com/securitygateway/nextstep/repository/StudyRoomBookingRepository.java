package com.securitygateway.nextstep.repository;

import com.securitygateway.nextstep.model.BookingStatus;
import com.securitygateway.nextstep.model.StudyRoomBooking;
import com.securitygateway.nextstep.model.User;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudyRoomBookingRepository extends JpaRepository<StudyRoomBooking, Long> {

    // ================= EXPIRE BOOKINGS =================

    @Modifying
    @Query("""
        update StudyRoomBooking b
        set b.status = com.securitygateway.nextstep.model.BookingStatus.EXPIRED
        where b.status = com.securitygateway.nextstep.model.BookingStatus.ACTIVE
          and b.expireAt < :now
    """)
    int markExpired(@Param("now") LocalDateTime now);

    @Modifying
    @Query("""
        delete from StudyRoomBooking b
        where b.status = com.securitygateway.nextstep.model.BookingStatus.EXPIRED
          and b.expireAt < :now
    """)
    int deleteExpired(@Param("now") LocalDateTime now);

    // ================= OVERLAP CHECK =================

    @Query("""
        select (count(b) > 0) from StudyRoomBooking b
        where b.room = :room
          and b.date = :date
          and b.status = com.securitygateway.nextstep.model.BookingStatus.ACTIVE
          and (b.startTime < :endTime and b.endTime > :startTime)
    """)
    boolean hasOverlap(@Param("room") String room,
                       @Param("date") LocalDate date,
                       @Param("startTime") LocalTime startTime,
                       @Param("endTime") LocalTime endTime);

    @Query("""
        select (count(b) > 0) from StudyRoomBooking b
        where b.id <> :id
          and b.room = :room
          and b.date = :date
          and b.status = com.securitygateway.nextstep.model.BookingStatus.ACTIVE
          and (b.startTime < :endTime and b.endTime > :startTime)
    """)
    boolean hasOverlapExcludingId(@Param("id") Long id,
                                  @Param("room") String room,
                                  @Param("date") LocalDate date,
                                  @Param("startTime") LocalTime startTime,
                                  @Param("endTime") LocalTime endTime);

    // ================= USER BOOKINGS =================

    List<StudyRoomBooking> findByUserIdOrderByDateDescStartTimeDesc(Long userId);
    Optional<StudyRoomBooking> findByIdAndUserId(Long id, Long userId);

    // ================= ADMIN / GLOBAL =================

    List<StudyRoomBooking> findAllByOrderByDateDescStartTimeDesc();

    List<StudyRoomBooking> findByDateAndStatusOrderByRoomAscStartTimeAsc(LocalDate date,
                                                                         BookingStatus status);

    List<StudyRoomBooking> findByStatusOrderByDateDescStartTimeDesc(BookingStatus status);

    // ✅ New method to fetch bookings by User entity for admin view
    List<StudyRoomBooking> findAllByUserOrderByDateDescStartTimeDesc(User user);
}