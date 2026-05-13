package se.iths.armin.projectmanagerapi.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.iths.armin.projectmanagerapi.dto.comment.CommentRequestDto;
import se.iths.armin.projectmanagerapi.dto.comment.CommentResponseDto;
import se.iths.armin.projectmanagerapi.dto.comment.CommentUpdateDto;
import se.iths.armin.projectmanagerapi.entity.AppUser;
import se.iths.armin.projectmanagerapi.entity.Comment;
import se.iths.armin.projectmanagerapi.entity.Task;
import se.iths.armin.projectmanagerapi.service.AppUserService;
import se.iths.armin.projectmanagerapi.service.TaskService;

@Component
@RequiredArgsConstructor
public class CommentMapperImpl
        implements EntityMapper<Comment, CommentRequestDto, CommentResponseDto> {

    private final AppUserService appUserService;
    private final TaskService taskService;


    @Override
    public Comment toEntity(CommentRequestDto commentRequestDto) {
        if (commentRequestDto == null) {
            return null;
        }
        Task task = taskService.getTask(commentRequestDto.taskId());
        AppUser appUser = appUserService.getAppUser(commentRequestDto.fromId());

        Comment comment = new Comment();
        comment.setContent(commentRequestDto.content());
        comment.setTask(task);
        comment.setAppUser(appUser);


        return comment;
    }

    @Override
    public CommentResponseDto toDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        CommentResponseDto commentResponseDto = new CommentResponseDto(
                comment.getContent(), comment.getAppUser().getUserid(), comment.getAppUser().getFirstname(),
                comment.getAppUser().getLastname(), comment.getTask().getTaskId(), comment.getCreatedAt()
        );
        return commentResponseDto;
    }

    public void update(CommentUpdateDto commentUpdateDto, Comment comment) {
        if (commentUpdateDto == null) {
            return;
        }
        comment.setContent(commentUpdateDto.content());
    }
}
