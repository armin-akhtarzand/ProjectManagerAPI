package se.iths.armin.projectmanagerapi.dto.projectuser;

import se.iths.armin.projectmanagerapi.entity.enums.ProjectRole;
import se.iths.armin.projectmanagerapi.entity.enums.ProjectStatus;

import java.time.LocalDateTime;

public record UserProjectResponseDto(
        Long projectId,
        String projectTitle,
        ProjectStatus status,
        ProjectRole role,
        LocalDateTime joinedAt
) {
}
