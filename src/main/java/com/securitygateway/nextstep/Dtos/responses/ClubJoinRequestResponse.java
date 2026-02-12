package com.securitygateway.nextstep.Dtos.responses;

import com.securitygateway.nextstep.model.ClubJoinRequest;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class ClubJoinRequestResponse {
    private Long id;
    private String studentEmail;
    private String clubName;
    private ClubJoinRequest.Status status;

    public static ClubJoinRequestResponse fromEntity(ClubJoinRequest request){
        return ClubJoinRequestResponse.builder()
                .id(request.getId())
                .studentEmail(request.getStudentEmail())
                .clubName(request.getClub().getClubName())
                .status(request.getStatus())
                .build();
    }
}
