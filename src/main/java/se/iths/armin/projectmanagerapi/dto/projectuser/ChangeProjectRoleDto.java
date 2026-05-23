package se.iths.armin.projectmanagerapi.dto.projectuser;

import jakarta.validation.constraints.NotNull;
import se.iths.armin.projectmanagerapi.entity.enums.ProjectRole;

public record ChangeProjectRoleDto(
        @NotNull(message = "New Project role is required")
        ProjectRole role
) {
}
