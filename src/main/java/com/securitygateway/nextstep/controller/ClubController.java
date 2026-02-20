package com.securitygateway.nextstep.controller;

import com.securitygateway.nextstep.Dtos.requests.ClubJoinRequestDTO;
import com.securitygateway.nextstep.Dtos.requests.CreateClubRequest;
import com.securitygateway.nextstep.Dtos.responses.ClubJoinRequestResponse;
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

    // ✅ Get all clubs
    @GetMapping
    public ResponseEntity<List<ClubResponse>> getAllClubs() {
        return clubService.getAllClubs();
    }

    // ✅ Only STUDENT can send join request
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/join")
    public ResponseEntity<GeneralAPIResponse> joinClub(
            @Valid @RequestBody ClubJoinRequestDTO dto) {
        return clubService.sendJoinRequest(dto);
    }

    // ✅ STUDENT + ADMIN can view APPROVED events
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/events")
    public ResponseEntity<List<EventResponse>> getApprovedEvents() {
        return eventService.getApprovedEvents();
    }

    // ✅ Only ADMIN can create club
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<GeneralAPIResponse> createClub(
            @Valid @RequestBody CreateClubRequest req) {
        return clubService.addClub(req);
    }

    // ✅ ADMIN approves join request
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/join/{id}/approve")
    public ResponseEntity<GeneralAPIResponse> approveJoin(
            @PathVariable Long id) {
        return clubService.approveJoinRequest(id);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/join/pending")
    public ResponseEntity<List<ClubJoinRequestResponse>> getPendingJoinRequests(){
        return clubService.getPendingRequests();
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralAPIResponse> updateClub(
            @PathVariable Long id,
            @RequestBody CreateClubRequest req) {
        return clubService.updateClub(id, req);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeneralAPIResponse> deleteClub(@PathVariable Long id) {
        return clubService.deleteClub(id);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/join/{id}/reject")
    public ResponseEntity<GeneralAPIResponse> rejectJoin(@PathVariable Long id){
        return clubService.rejectJoinRequest(id);
    }


}
