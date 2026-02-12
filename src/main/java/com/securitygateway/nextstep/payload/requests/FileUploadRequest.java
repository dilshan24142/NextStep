package com.securitygateway.nextstep.payload.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadRequest {

    @NotNull(message = "Folder ID cannot be null")
    private Long folderId;

    private String description;
}