package se.iths.armin.projectmanagerapi.dto.appuser;

import se.iths.armin.projectmanagerapi.entity.enums.UserPosition;
import se.iths.armin.projectmanagerapi.entity.enums.UserStatus;

public record AppUserResponseDto(
        String email,
        String firstname,
        String lastname,
        UserPosition position,
        UserStatus status
) {
}
