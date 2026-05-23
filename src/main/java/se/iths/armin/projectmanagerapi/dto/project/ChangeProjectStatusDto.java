package se.iths.armin.projectmanagerapi.dto.project;

import jakarta.validation.constraints.NotNull;
import se.iths.armin.projectmanagerapi.entity.enums.ProjectStatus;

public record ChangeProjectStatusDto(
        @NotNull(message = "New Project status is required")
        ProjectStatus status
) {
}
