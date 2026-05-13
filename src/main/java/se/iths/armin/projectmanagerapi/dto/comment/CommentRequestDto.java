package se.iths.armin.projectmanagerapi.dto.comment;

public record CommentRequestDto(
        String content,
        Long fromId,
        Long taskId
) {
}
