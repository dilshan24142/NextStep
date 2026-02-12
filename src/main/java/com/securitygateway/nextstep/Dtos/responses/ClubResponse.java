package com.securitygateway.nextstep.Dtos.responses;

import com.securitygateway.nextstep.model.Club;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class ClubResponse {
    private Long id;
    private String clubName;
    private String description;

    public static ClubResponse fromEntity(Club club){
        return ClubResponse.builder()
                .id(club.getId())
                .clubName(club.getClubName())
                .description(club.getDescription())
                .build();
    }
}
