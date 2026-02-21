package com.securitygateway.nextstep.service;

import com.securitygateway.nextstep.Dtos.requests.StudentCreateEventRequest;
import com.securitygateway.nextstep.Dtos.responses.EventResponse;
import com.securitygateway.nextstep.Dtos.responses.GeneralAPIResponse;
import com.securitygateway.nextstep.model.Club;
import com.securitygateway.nextstep.model.Event;
import com.securitygateway.nextstep.repository.ClubRepository;
import com.securitygateway.nextstep.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventRepository eventRepository;
    private final ClubRepository clubRepository;
    private final EmailService emailService;

    // ===============================
    // STUDENT CREATE EVENT REQUEST
    // ===============================
    public ResponseEntity<GeneralAPIResponse> createEventRequest(StudentCreateEventRequest req){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Club club = clubRepository.findById(req.getClubId())
                .orElseThrow(() -> new RuntimeException("Club not found"));

        Event event = Event.builder()
                .club(club)
                .title(req.getTitle())
                .description(req.getDescription())
                .eventDate(req.getEventDate())
                .createdBy(email)
                .status(Event.Status.PENDING)
                .build();

        eventRepository.save(event);

        return ResponseEntity.ok(
                GeneralAPIResponse.builder().message("Event request submitted").build()
        );
    }

    // ADMIN APPROVE EVENT
    public ResponseEntity<GeneralAPIResponse> approveEvent(Long id){
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        event.setStatus(Event.Status.APPROVED);
        eventRepository.save(event);

        // Send custom email
        try {
            emailService.sendCustomEmail(
                    event.getCreatedBy(),
                    "Event Request Update",
                    "Your event '" + event.getTitle() + "' has been APPROVED."
            );
        } catch (Exception e){
            log.error("Failed to send email: {}", e.getMessage());
        }

        return ResponseEntity.ok(
                GeneralAPIResponse.builder().message("Event approved").build()
        );
    }

    // REJECT EVENT
    public ResponseEntity<GeneralAPIResponse> rejectEvent(Long id){
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        event.setStatus(Event.Status.REJECTED);
        eventRepository.save(event);

        try {
            emailService.sendCustomEmail(
                    event.getCreatedBy(),
                    "Event Request Update",
                    "Your event '" + event.getTitle() + "' has been DECLINED."
            );
        } catch (Exception e){
            log.error("Failed to send email: {}", e.getMessage());
        }

        return ResponseEntity.ok(
                GeneralAPIResponse.builder().message("Event rejected").build()
        );
    }

    public ResponseEntity<List<EventResponse>> getApprovedEvents(){
        return ResponseEntity.ok(
                eventRepository.findByStatus(Event.Status.APPROVED)
                        .stream()
                        .map(EventResponse::fromEntity)
                        .toList()
        );
    }

    public ResponseEntity<List<EventResponse>> getPendingEvents(){
        return ResponseEntity.ok(
                eventRepository.findByStatus(Event.Status.PENDING)
                        .stream()
                        .map(EventResponse::fromEntity)
                        .toList()
        );
    }

    public ResponseEntity<List<EventResponse>> getMyEvents(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseEntity.ok(
                eventRepository.findByCreatedBy(email)
                        .stream()
                        .map(EventResponse::fromEntity)
                        .toList()
        );
    }

    public ResponseEntity<GeneralAPIResponse> deleteEvent(Long id){

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        eventRepository.delete(event);

        return ResponseEntity.ok(
                GeneralAPIResponse.builder()
                        .message("Event deleted successfully")
                        .build()
        );
    }
}