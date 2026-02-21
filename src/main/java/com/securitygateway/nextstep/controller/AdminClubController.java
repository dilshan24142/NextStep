package com.securitygateway.nextstep.controller;

import com.securitygateway.nextstep.Dtos.responses.ClubJoinRequestResponse;
import com.securitygateway.nextstep.Dtos.responses.GeneralAPIResponse;
import com.securitygateway.nextstep.service.AdminClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clubs/requests")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminClubController {

    private final AdminClubService adminClubService;

    @GetMapping
    public ResponseEntity<List<ClubJoinRequestResponse>> getPendingRequests() {
        return adminClubService.getPendingJoinRequests();
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<GeneralAPIResponse> approve(@PathVariable Long id) {
        return adminClubService.approveRequest(id);
    }

    @PostMapping("/decline/{id}")
    public ResponseEntity<GeneralAPIResponse> decline(@PathVariable Long id) {
        return adminClubService.declineRequest(id);
    }
}
