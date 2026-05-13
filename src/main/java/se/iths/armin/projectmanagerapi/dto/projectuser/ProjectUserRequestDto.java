package se.iths.armin.projectmanagerapi.dto.projectuser;

import jakarta.validation.constraints.NotBlank;

public record ProjectUserRequestDto(
        @NotBlank(message = "Project is required")
        Long projectId,
        @NotBlank(message = "User is required")
        Long appUserId
) {
}
