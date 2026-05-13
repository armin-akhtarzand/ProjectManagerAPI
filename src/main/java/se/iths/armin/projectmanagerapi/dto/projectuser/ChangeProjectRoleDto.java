package se.iths.armin.projectmanagerapi.dto.projectuser;

import jakarta.validation.constraints.NotBlank;
import se.iths.armin.projectmanagerapi.entity.enums.ProjectRole;

public record ChangeProjectRoleDto(
        @NotBlank(message = "New Project role is required")
        ProjectRole role
) {
}
