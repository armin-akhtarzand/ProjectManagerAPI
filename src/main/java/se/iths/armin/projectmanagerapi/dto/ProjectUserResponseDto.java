package se.iths.armin.projectmanagerapi.dto;

import se.iths.armin.projectmanagerapi.entity.enums.ProjectRole;

import java.time.LocalDateTime;

public record ProjectUserResponseDto(
        Long projectUserId,
        Long projectId,
        String projectName,
        Long userId,
        String firstName,
        String lastName,
        ProjectRole role,
        LocalDateTime joinedAt
) {
}
