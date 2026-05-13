package se.iths.armin.projectmanagerapi.dto.comment;

import jakarta.validation.constraints.NotBlank;

public record CommentUpdateDto(
        @NotBlank(message = "Content can not be blank")
        String content
) {
}
