package se.iths.armin.projectmanagerapi.dto.projectuser;

import jakarta.validation.constraints.NotNull;

public record ProjectUserRequestDto(
        @NotNull(message = "Project is required")
        Long projectId,
        @NotNull(message = "User is required")
        Long appUserId
) {
}
