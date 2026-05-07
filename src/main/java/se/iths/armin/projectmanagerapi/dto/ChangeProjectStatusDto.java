package se.iths.armin.projectmanagerapi.dto;

import jakarta.validation.constraints.NotBlank;
import se.iths.armin.projectmanagerapi.entity.enums.ProjectStatus;

public record ChangeProjectStatusDto(
        @NotBlank
        ProjectStatus status
) {
}
