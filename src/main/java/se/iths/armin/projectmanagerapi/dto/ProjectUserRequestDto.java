package se.iths.armin.projectmanagerapi.dto;

import jakarta.validation.constraints.NotBlank;

public record ProjectUserRequestDto(
        @NotBlank
        Long projectId,
        @NotBlank
        Long appUserId
) {
}
