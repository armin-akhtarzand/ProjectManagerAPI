package se.iths.armin.projectmanagerapi.dto.project;

import se.iths.armin.projectmanagerapi.entity.enums.ProjectStatus;

import java.time.LocalDateTime;

public record ProjectResponseDto(
        String title,
        String description,
        ProjectStatus projectStatus,
        LocalDateTime createdAt
) {
}
