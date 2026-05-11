package se.iths.armin.projectmanagerapi.dto;

import se.iths.armin.projectmanagerapi.entity.enums.ProjectRole;

import java.time.LocalDateTime;

public record ProjectMemberResponseDto(
        Long userId,
        String firstname,
        String lastname,
        ProjectRole role,
        LocalDateTime joinedAt
) {
}
