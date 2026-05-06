package se.iths.armin.projectmanagerapi.dto;

import se.iths.armin.projectmanagerapi.entity.enums.UserPosition;
import se.iths.armin.projectmanagerapi.entity.enums.UserStatus;

public record AppUserRespondDto(
        String email,
        String firstname,
        String lastname,
        UserPosition position,
        UserStatus status
) {
}
