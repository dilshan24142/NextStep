package com.securitygateway.nextstep.controller;

import com.securitygateway.nextstep.Dtos.requests.StudentCreateEventRequest;
import com.securitygateway.nextstep.Dtos.responses.EventResponse;
import com.securitygateway.nextstep.Dtos.responses.GeneralAPIResponse;
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

    // STUDENT creates request
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/request")
    public ResponseEntity<GeneralAPIResponse> createEventRequest(
            @Valid @RequestBody StudentCreateEventRequest req){
        return eventService.createEventRequest(req);
    }

    // ADMIN approve
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/approve")
    public ResponseEntity<GeneralAPIResponse> approveEvent(@PathVariable Long id){
        return eventService.approveEvent(id);
    }

    // STUDENT dashboard
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/approved")
    public ResponseEntity<List<EventResponse>> getApprovedEvents(){
        return eventService.getApprovedEvents();
    }

    // ADMIN dashboard
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<List<EventResponse>> getPendingEvents(){
        return eventService.getPendingEvents();
    }
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<EventResponse>> getMyEvents(){
        return eventService.getMyEvents();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralAPIResponse> deleteEvent(@PathVariable Long id){
        return eventService.deleteEvent(id);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/reject")
    public ResponseEntity<GeneralAPIResponse> rejectEvent(@PathVariable Long id){
        return eventService.rejectEvent(id);
    }


}
