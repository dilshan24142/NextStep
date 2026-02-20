package com.securitygateway.nextstep.service;

import com.securitygateway.nextstep.Dtos.requests.StudentCreateEventRequest;
import com.securitygateway.nextstep.Dtos.responses.EventResponse;
import com.securitygateway.nextstep.Dtos.responses.GeneralAPIResponse;
import com.securitygateway.nextstep.model.Club;
import com.securitygateway.nextstep.model.Event;
import com.securitygateway.nextstep.repository.ClubRepository;
import com.securitygateway.nextstep.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ClubRepository clubRepository;

    /* ===============================
       STUDENT CREATE EVENT REQUEST
       =============================== */

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

    /* ===============================
       ADMIN APPROVE EVENT
       =============================== */

    public ResponseEntity<GeneralAPIResponse> approveEvent(Long id){

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        event.setStatus(Event.Status.APPROVED);
        eventRepository.save(event);

        return ResponseEntity.ok(
                GeneralAPIResponse.builder().message("Event approved").build()
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


}
