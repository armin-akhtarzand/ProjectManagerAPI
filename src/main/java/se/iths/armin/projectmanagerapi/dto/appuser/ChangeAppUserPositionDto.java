package se.iths.armin.projectmanagerapi.dto.appuser;

import jakarta.validation.constraints.NotNull;
import se.iths.armin.projectmanagerapi.entity.enums.UserPosition;

public record ChangeAppUserPositionDto(
        @NotNull(message = "New User position is required")
        UserPosition position
) {
}
