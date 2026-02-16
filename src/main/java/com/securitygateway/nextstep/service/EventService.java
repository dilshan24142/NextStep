package com.securitygateway.nextstep.service;

import com.securitygateway.nextstep.model.Club;
import com.securitygateway.nextstep.model.Event;
import com.securitygateway.nextstep.Dtos.requests.CreateEventRequest;
import com.securitygateway.nextstep.Dtos.requests.UpdateEventRequest;
import com.securitygateway.nextstep.Dtos.responses.EventResponse;
import com.securitygateway.nextstep.Dtos.responses.GeneralAPIResponse;
import com.securitygateway.nextstep.repository.ClubRepository;
import com.securitygateway.nextstep.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ClubRepository clubRepository;

    public ResponseEntity<GeneralAPIResponse> createEvent(CreateEventRequest req){
        Club club = clubRepository.findById(req.getClubId())
                .orElseThrow(() -> new RuntimeException("Club not found"));

        Event event = Event.builder()
                .club(club)
                .title(req.getTitle())
                .description(req.getDescription())
                .eventDate(req.getEventDate())
                .build();

        eventRepository.save(event);
        return ResponseEntity.ok(GeneralAPIResponse.builder().message("Event created").build());
    }

    public ResponseEntity<GeneralAPIResponse> updateEvent(Long id, UpdateEventRequest req){
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        event.setTitle(req.getTitle());
        event.setDescription(req.getDescription());
        event.setEventDate(req.getEventDate());

        eventRepository.save(event);
        return ResponseEntity.ok(GeneralAPIResponse.builder().message("Event updated").build());
    }

    public ResponseEntity<GeneralAPIResponse> deleteEvent(Long id){
        eventRepository.deleteById(id);
        return ResponseEntity.ok(GeneralAPIResponse.builder().message("Event deleted").build());
    }

    public ResponseEntity<List<EventResponse>> getAllEvents(){
        List<EventResponse> events = eventRepository.findAll().stream()
                .map(EventResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(events);
    }
}
