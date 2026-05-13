package se.iths.armin.projectmanagerapi.dto.task;

public record ChangeTaskAssigneeDto(
        Long assigneeId,
        String assigneeFirstname,
        String assigneeLastname
) {
}
