package com.securitygateway.nextstep.repository;

import com.securitygateway.nextstep.model.BookingStatus;
import com.securitygateway.nextstep.model.StudyRoomBooking;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.*;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudyRoomBookingRepository extends JpaRepository<StudyRoomBooking, Long> {

    // ✅ Mark EXPIRED
    @Modifying
    @Query("""
        update StudyRoomBooking b
        set b.status = com.securitygateway.nextstep.model.BookingStatus.EXPIRED
        where b.status = com.securitygateway.nextstep.model.BookingStatus.ACTIVE
          and b.expireAt < :now
    """)
    int markExpired(@Param("now") LocalDateTime now);

    // ✅ Cleanup job delete
    @Modifying
    @Query("""
        delete from StudyRoomBooking b
        where b.status = com.securitygateway.nextstep.model.BookingStatus.EXPIRED
          and b.expireAt < :now
    """)
    int deleteExpired(@Param("now") LocalDateTime now);

    // ✅ overlap check
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

    // ✅ overlap check (exclude one booking id) - for update
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

    List<StudyRoomBooking> findByUserIdOrderByDateDescStartTimeDesc(Long userId);

    Optional<StudyRoomBooking> findByIdAndUserId(Long id, Long userId);

    List<StudyRoomBooking> findAllByOrderByDateDescStartTimeDesc();

    List<StudyRoomBooking> findByDateAndStatusOrderByRoomAscStartTimeAsc(LocalDate date, BookingStatus status);

    List<StudyRoomBooking> findByStatusOrderByDateDescStartTimeDesc(BookingStatus status);
}
