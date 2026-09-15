package se.iths.armin.projectmanagerapi.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TaskRequestDto(
        @NotNull(message = "Creator required")
        Long creatorId,
        @NotNull(message = "Assignee required")
        Long assigneeId,
        @NotBlank(message = "Title is required")
        String title,
        @NotBlank(message = "Description is required")
        String description,

        LocalDate deadline
) {
}
