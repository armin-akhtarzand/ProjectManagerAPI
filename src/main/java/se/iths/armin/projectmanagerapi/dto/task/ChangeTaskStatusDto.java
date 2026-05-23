package se.iths.armin.projectmanagerapi.dto.task;

import jakarta.validation.constraints.NotNull;
import se.iths.armin.projectmanagerapi.entity.enums.TaskStatus;

public record ChangeTaskStatusDto(
        @NotNull(message = "New Task status is required")
        TaskStatus status
) {
}
