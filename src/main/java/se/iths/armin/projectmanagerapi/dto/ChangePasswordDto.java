package se.iths.armin.projectmanagerapi.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordDto(
        @NotBlank
        String password
) {
}
