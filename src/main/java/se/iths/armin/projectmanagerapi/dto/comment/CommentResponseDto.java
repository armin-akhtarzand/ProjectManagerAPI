package se.iths.armin.projectmanagerapi.dto.comment;

import java.time.LocalDateTime;

public record CommentResponseDto(
        String content,
        Long fromId,
        String fromFirstname,
        String fromLastname,
        Long taskId,
        LocalDateTime createdAt

) {
}
