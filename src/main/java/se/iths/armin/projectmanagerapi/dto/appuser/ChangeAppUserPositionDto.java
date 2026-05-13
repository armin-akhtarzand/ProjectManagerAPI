package se.iths.armin.projectmanagerapi.dto.appuser;

import jakarta.validation.constraints.NotBlank;
import se.iths.armin.projectmanagerapi.entity.enums.UserPosition;

public record ChangeAppUserPositionDto(
        @NotBlank(message = "New User position is required")
        UserPosition position
) {
}
