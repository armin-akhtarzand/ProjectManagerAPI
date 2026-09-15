package se.iths.armin.projectmanagerapi.dto.task;

import jakarta.validation.constraints.NotNull;

public record ChangeTaskAssigneeDto(
        @NotNull
        Long assigneeId
) {
}
