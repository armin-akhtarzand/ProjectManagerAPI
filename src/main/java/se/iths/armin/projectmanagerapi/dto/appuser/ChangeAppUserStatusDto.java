package se.iths.armin.projectmanagerapi.dto.appuser;

import jakarta.validation.constraints.NotNull;
import se.iths.armin.projectmanagerapi.entity.enums.UserStatus;

public record ChangeAppUserStatusDto(
        @NotNull(message = "New User status is required")
        UserStatus status
) {
}
