package se.iths.armin.projectmanagerapi.dto;

import jakarta.validation.constraints.NotBlank;
import se.iths.armin.projectmanagerapi.entity.enums.UserStatus;

public record ChangeAppUserStatusDto(
        @NotBlank
        UserStatus status
) {
}
