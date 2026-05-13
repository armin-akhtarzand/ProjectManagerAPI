package se.iths.armin.projectmanagerapi.dto;

import se.iths.armin.projectmanagerapi.entity.enums.TaskStatus;

import java.time.LocalDate;

public record TaskResponseDto(
        Long creatorId,
        String creatorName,
        Long assigneeId,
        String assigneeFirstname,
        String assigneLastname,
        Long projectId,
        String projectName,
        String title,
        String description,
        TaskStatus taskStatus,
        LocalDate deadline

) {
}
