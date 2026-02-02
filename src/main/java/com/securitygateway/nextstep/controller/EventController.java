package com.securitygateway.nextstep.controller;

import com.securitygateway.nextstep.payload.requests.CreateEventRequest;
import com.securitygateway.nextstep.payload.requests.UpdateEventRequest;
import com.securitygateway.nextstep.payload.responses.EventResponse;
import com.securitygateway.nextstep.payload.responses.GeneralAPIResponse;
import com.securitygateway.nextstep.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<GeneralAPIResponse> createEvent(@Valid @RequestBody CreateEventRequest req){
        return eventService.createEvent(req);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<GeneralAPIResponse> updateEvent(@PathVariable Long id,
                                                          @Valid @RequestBody UpdateEventRequest req){
        return eventService.updateEvent(id, req);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralAPIResponse> deleteEvent(@PathVariable Long id){
        return eventService.deleteEvent(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents(){
        return eventService.getAllEvents();
    }
}
