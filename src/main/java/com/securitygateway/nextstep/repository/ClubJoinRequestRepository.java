package com.securitygateway.nextstep.repository;

import com.securitygateway.nextstep.model.ClubJoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClubJoinRequestRepository extends JpaRepository<ClubJoinRequest, Long> {
    List<ClubJoinRequest> findByStatus(ClubJoinRequest.Status status);
    ClubJoinRequest findByClubIdAndEmail(Long clubId, String email);
}