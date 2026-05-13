package se.iths.armin.projectmanagerapi.dto;

public record ChangeTaskAssigneeDto(
        Long assigneeId,
        String assigneeFirstname,
        String assigneeLastname
) {
}
