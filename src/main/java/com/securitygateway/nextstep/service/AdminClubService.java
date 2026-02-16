package com.securitygateway.nextstep.service;

import com.securitygateway.nextstep.model.ClubJoinRequest;
import com.securitygateway.nextstep.Dtos.responses.ClubJoinRequestResponse;
import com.securitygateway.nextstep.Dtos.responses.GeneralAPIResponse;
import com.securitygateway.nextstep.repository.ClubJoinRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminClubService {

    private final ClubJoinRequestRepository requestRepository;

    public ResponseEntity<List<ClubJoinRequestResponse>> getPendingJoinRequests() {
        List<ClubJoinRequestResponse> pending = requestRepository.findByStatus(ClubJoinRequest.Status.PENDING)
                .stream().map(ClubJoinRequestResponse::fromEntity).toList();
        return ResponseEntity.ok(pending);
    }

    public ResponseEntity<GeneralAPIResponse> approveRequest(Long id) {
        ClubJoinRequest req = requestRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        req.setStatus(ClubJoinRequest.Status.APPROVED);
        requestRepository.save(req);
        return ResponseEntity.ok(GeneralAPIResponse.builder().message("Approved").build());
    }

    public ResponseEntity<GeneralAPIResponse> declineRequest(Long id) {
        ClubJoinRequest req = requestRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        req.setStatus(ClubJoinRequest.Status.DECLINED);
        requestRepository.save(req);
        return ResponseEntity.ok(GeneralAPIResponse.builder().message("Declined").build());
    }
}
