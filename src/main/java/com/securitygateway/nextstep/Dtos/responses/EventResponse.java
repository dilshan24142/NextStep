package com.securitygateway.nextstep.Dtos.responses;

import com.securitygateway.nextstep.model.Event;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder
public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime eventDate;

    public static EventResponse fromEntity(Event event){
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .build();
    }
}
