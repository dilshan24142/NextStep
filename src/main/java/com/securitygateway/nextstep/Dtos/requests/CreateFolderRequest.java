package com.securitygateway.nextstep.Dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateFolderRequest {

    @NotBlank(message = "Folder name cannot be blank")
    @Size(min = 1, max = 100, message = "Folder name must be between 1 and 100 characters")
    private String name;

    private String description;

    private Long parentFolderId; // null for root folder
}