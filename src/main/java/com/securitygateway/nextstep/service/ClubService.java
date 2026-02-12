package com.securitygateway.nextstep.service;

import com.securitygateway.nextstep.model.Club;
import com.securitygateway.nextstep.model.ClubJoinRequest;
import com.securitygateway.nextstep.Dtos.requests.ClubJoinRequestDTO;
import com.securitygateway.nextstep.Dtos.requests.CreateClubRequest;
import com.securitygateway.nextstep.Dtos.responses.ClubResponse;
import com.securitygateway.nextstep.Dtos.responses.GeneralAPIResponse;
import com.securitygateway.nextstep.repository.ClubJoinRequestRepository;
import com.securitygateway.nextstep.repository.ClubRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClubService {

    private final ClubRepository clubRepository;
    private final ClubJoinRequestRepository requestRepository;

    public ResponseEntity<List<ClubResponse>> getAllClubs() {
        return ResponseEntity.ok(
                clubRepository.findAll().stream()
                        .map(ClubResponse::fromEntity)
                        .toList()
        );
    }

    // ✅ admin add club
    public ResponseEntity<GeneralAPIResponse> addClub(CreateClubRequest req) {
        Club club = Club.builder()
                .clubName(req.getClubName())
                .description(req.getDescription())
                .build();
        clubRepository.save(club);
        return ResponseEntity.ok(GeneralAPIResponse.builder().message("Club created").build());
    }

    // ✅ student join request (UNCHANGED)
    public ResponseEntity<GeneralAPIResponse> sendJoinRequest(ClubJoinRequestDTO dto) {
        Club club = clubRepository.findById(dto.getClubId())
                .orElseThrow(() -> new RuntimeException("Club not found"));

        if (requestRepository.findByClubIdAndStudentEmail(dto.getClubId(), dto.getStudentEmail()) != null) {
            return ResponseEntity.badRequest()
                    .body(GeneralAPIResponse.builder().message("Already requested").build());
        }

        ClubJoinRequest req = ClubJoinRequest.builder()
                .club(club)
                .studentEmail(dto.getStudentEmail())
                .status(ClubJoinRequest.Status.PENDING)
                .build();

        requestRepository.save(req);
        return ResponseEntity.ok(GeneralAPIResponse.builder().message("Request submitted").build());
    }
}
