package se.iths.armin.projectmanagerapi.dto.task;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record TaskRequestDto(
        @NotBlank(message = "Creator required")
        Long creatorId,
        @NotBlank(message = "Assignee required")
        Long assigneeId,
        @NotBlank(message = "Project required")
        Long projectId,
        @NotBlank(message = "Title is required")
        String title,
        @NotBlank(message = "Description is required")
        String description,

        LocalDate deadline
) {
}
