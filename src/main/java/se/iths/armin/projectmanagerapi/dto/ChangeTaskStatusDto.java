package se.iths.armin.projectmanagerapi.dto;

import se.iths.armin.projectmanagerapi.entity.enums.TaskStatus;

public record ChangeTaskStatusDto(
        TaskStatus status
) {
}
