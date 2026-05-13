package se.iths.armin.projectmanagerapi.dto.task;

import se.iths.armin.projectmanagerapi.entity.enums.TaskStatus;

public record ChangeTaskStatusDto(
        TaskStatus status
) {
}
