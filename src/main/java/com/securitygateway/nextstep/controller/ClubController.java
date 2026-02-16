package com.securitygateway.nextstep.controller;

import com.securitygateway.nextstep.Dtos.requests.ClubJoinRequestDTO;
import com.securitygateway.nextstep.Dtos.responses.ClubResponse;
import com.securitygateway.nextstep.Dtos.responses.EventResponse;
import com.securitygateway.nextstep.Dtos.responses.GeneralAPIResponse;
import com.securitygateway.nextstep.service.ClubService;
import com.securitygateway.nextstep.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<ClubResponse>> getAllClubs() {
        return clubService.getAllClubs();
    }

    // ✅ only STUDENT can send join requests
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/join")
    public ResponseEntity<GeneralAPIResponse> joinClub(@Valid @RequestBody ClubJoinRequestDTO dto){
        return clubService.sendJoinRequest(dto);
    }

    // ✅ both ADMIN + STUDENT can view events
    @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    @GetMapping("/events")
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        return eventService.getAllEvents();
    }
}
