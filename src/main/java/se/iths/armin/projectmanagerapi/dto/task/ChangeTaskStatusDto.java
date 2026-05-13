package se.iths.armin.projectmanagerapi.dto.task;

import jakarta.validation.constraints.NotBlank;
import se.iths.armin.projectmanagerapi.entity.enums.TaskStatus;

public record ChangeTaskStatusDto(
        @NotBlank(message = "New Task status is required")
        TaskStatus status
) {
}
