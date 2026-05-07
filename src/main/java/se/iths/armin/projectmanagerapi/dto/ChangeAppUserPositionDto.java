package se.iths.armin.projectmanagerapi.dto;

import jakarta.validation.constraints.NotBlank;
import se.iths.armin.projectmanagerapi.entity.enums.UserPosition;

public record ChangeAppUserPositionDto(
        @NotBlank
        UserPosition position
) {
}
