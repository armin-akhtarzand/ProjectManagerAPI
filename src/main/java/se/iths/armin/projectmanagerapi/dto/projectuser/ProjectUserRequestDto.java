package se.iths.armin.projectmanagerapi.dto.projectuser;

import jakarta.validation.constraints.NotNull;

public record ProjectUserRequestDto(
        @NotNull(message = "User is required")
        Long appUserId,
        @NotNull(message = "Project is required")
        Long projectId
) {
}
