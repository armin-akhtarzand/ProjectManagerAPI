package se.iths.armin.projectmanagerapi.dto.appuser;

import jakarta.validation.constraints.NotBlank;
import se.iths.armin.projectmanagerapi.entity.enums.UserStatus;

public record ChangeAppUserStatusDto(
        @NotBlank(message = "New User status is required")
        UserStatus status
) {
}
