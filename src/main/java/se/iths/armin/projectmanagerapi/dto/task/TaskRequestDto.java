package se.iths.armin.projectmanagerapi.dto.task;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record TaskRequestDto(
        Long creatorId,
        @NotBlank(message = "Assignee required")
        Long assigneeId,
        Long projectId,
        String title,
        String description,
        LocalDate deadline
) {
}
