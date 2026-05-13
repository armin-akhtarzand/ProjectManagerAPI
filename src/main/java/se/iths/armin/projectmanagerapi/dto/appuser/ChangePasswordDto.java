package se.iths.armin.projectmanagerapi.dto.appuser;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordDto(
        @NotBlank(message = "New password is required")
        String newPassword,

        @NotBlank(message = "Old password is required")
        String oldPassword
) {
}
