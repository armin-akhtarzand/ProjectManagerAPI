package se.iths.armin.projectmanagerapi.dto.projectuser;

import se.iths.armin.projectmanagerapi.entity.enums.ProjectRole;

public record ChangeProjectRoleDto(
        ProjectRole role
) {
}
