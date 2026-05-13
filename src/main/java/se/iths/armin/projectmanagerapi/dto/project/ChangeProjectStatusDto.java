package se.iths.armin.projectmanagerapi.dto.project;

import jakarta.validation.constraints.NotBlank;
import se.iths.armin.projectmanagerapi.entity.enums.ProjectStatus;

public record ChangeProjectStatusDto(
        @NotBlank(message = "New Project status is required")
        ProjectStatus status
) {
}
