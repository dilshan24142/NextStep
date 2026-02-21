package com.securitygateway.nextstep.service;

import com.securitygateway.nextstep.Dtos.responses.ClubJoinRequestResponse;
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
    private final EmailService emailService;

    // ✅ Get all clubs
    public ResponseEntity<List<ClubResponse>> getAllClubs() {
        return ResponseEntity.ok(
                clubRepository.findAll().stream()
                        .map(ClubResponse::fromEntity)
                        .toList()
        );
    }

    // ADMIN approve join request
    public ResponseEntity<GeneralAPIResponse> approveJoinRequest(Long id){
        ClubJoinRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(ClubJoinRequest.Status.APPROVED);
        requestRepository.save(request);

        // Send custom email (no OTP)
        try {
            emailService.sendCustomEmail(
                    request.getEmail(),
                    "Club Request Update",
                    "Your request to join " + request.getClub().getClubName() + " has been APPROVED."
            );
        } catch (Exception e){
            log.error("Failed to send email: {}", e.getMessage());
        }

        return ResponseEntity.ok(
                GeneralAPIResponse.builder()
                        .message("Join request approved")
                        .build()
        );
    }

    public ResponseEntity<GeneralAPIResponse> rejectJoinRequest(Long id){
        ClubJoinRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(ClubJoinRequest.Status.DECLINED);
        requestRepository.save(request);

        // Send custom email (no OTP)
        try {
            emailService.sendCustomEmail(
                    request.getEmail(),
                    "Club Request Update",
                    "Your request to join " + request.getClub().getClubName() + " has been DECLINED."
            );
        } catch (Exception e){
            log.error("Failed to send email: {}", e.getMessage());
        }

        return ResponseEntity.ok(
                GeneralAPIResponse.builder()
                        .message("Join request rejected")
                        .build()
        );
    }

    // ✅ Admin add club
    public ResponseEntity<GeneralAPIResponse> addClub(CreateClubRequest req) {
        Club club = Club.builder()
                .clubName(req.getClubName())
                .description(req.getDescription())
                .build();

        clubRepository.save(club);

        return ResponseEntity.ok(
                GeneralAPIResponse.builder()
                        .message("Club created")
                        .build()
        );
    }

    // ✅ Student join request
    public ResponseEntity<GeneralAPIResponse> sendJoinRequest(ClubJoinRequestDTO dto) {

        Club club = clubRepository.findById(dto.getClubId())
                .orElseThrow(() -> new RuntimeException("Club not found"));

        if (requestRepository.findByClubIdAndEmail(dto.getClubId(), dto.getEmail()) != null) {
            return ResponseEntity.badRequest()
                    .body(
                            GeneralAPIResponse.builder()
                                    .message("Already requested")
                                    .build()
                    );
        }

        ClubJoinRequest req = ClubJoinRequest.builder()
                .club(club)
                .email(dto.getEmail())
                .status(ClubJoinRequest.Status.PENDING)
                .build();

        requestRepository.save(req);

        return ResponseEntity.ok(
                GeneralAPIResponse.builder()
                        .message("Request submitted")
                        .build()
        );
    }

    public ResponseEntity<List<ClubJoinRequestResponse>> getPendingRequests(){
        return ResponseEntity.ok(
                requestRepository.findByStatus(ClubJoinRequest.Status.PENDING)
                        .stream()
                        .map(ClubJoinRequestResponse::fromEntity)
                        .toList()
        );
    }

    public ResponseEntity<GeneralAPIResponse> deleteJoinRequest(Long id){

        ClubJoinRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        requestRepository.delete(request);

        return ResponseEntity.ok(
                GeneralAPIResponse.builder()
                        .message("Join request deleted")
                        .build()
        );
    }

    public ResponseEntity<GeneralAPIResponse> updateClub(Long id, CreateClubRequest req) {

        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Club not found"));

        club.setClubName(req.getClubName());
        club.setDescription(req.getDescription());

        clubRepository.save(club);

        return ResponseEntity.ok(
                GeneralAPIResponse.builder()
                        .message("Club updated successfully")
                        .build()
        );
    }

    public ResponseEntity<GeneralAPIResponse> deleteClub(Long id) {

        clubRepository.deleteById(id);

        return ResponseEntity.ok(
                GeneralAPIResponse.builder()
                        .message("Club deleted successfully")
                        .build()
        );
    }

}