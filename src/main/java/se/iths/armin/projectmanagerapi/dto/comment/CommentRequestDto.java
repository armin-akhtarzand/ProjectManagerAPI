package se.iths.armin.projectmanagerapi.dto.comment;

import jakarta.validation.constraints.NotBlank;

public record CommentRequestDto(
        @NotBlank(message = "Content can not be blank")
        String content,
        @NotBlank(message = "Sender is required")
        Long fromId,
        @NotBlank(message = "Task is required")
        Long taskId
) {
}
