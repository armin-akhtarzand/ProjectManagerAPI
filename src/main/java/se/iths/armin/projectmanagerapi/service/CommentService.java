package se.iths.armin.projectmanagerapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.iths.armin.projectmanagerapi.dto.comment.CommentRequestDto;
import se.iths.armin.projectmanagerapi.dto.comment.CommentResponseDto;
import se.iths.armin.projectmanagerapi.dto.comment.CommentUpdateDto;
import se.iths.armin.projectmanagerapi.entity.AppUser;
import se.iths.armin.projectmanagerapi.entity.Comment;
import se.iths.armin.projectmanagerapi.entity.Project;
import se.iths.armin.projectmanagerapi.entity.Task;
import se.iths.armin.projectmanagerapi.exception.ResourceNotFoundException;
import se.iths.armin.projectmanagerapi.mapper.CommentMapperImpl;
import se.iths.armin.projectmanagerapi.repository.CommentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapperImpl commentMapper;
    private final ProjectUserService projectUserService;
    private final ProjectService projectService;
    private final AppUserService appUserService;
    private final TaskService taskService;
    private final AuthorizationService authorizationService;


    @Transactional
    public CommentResponseDto create(CommentRequestDto commentRequestDto, Long taskId, Long projectId,
                                     Long userId) {
        Project project = projectService.getProject(projectId);
        AppUser from = appUserService.getAppUser(userId);
        Task task = taskService.getTask(taskId);
        projectUserService.getProjectUser(from, project);

        if (!task.getProject().getProjectId().equals(projectId)) {
            throw new ResourceNotFoundException("Task does not belong to project");
        }


        Comment comment = commentMapper.toEntity(commentRequestDto);
        comment.setTask(task);
        comment.setAppUser(from);
        Comment saved = commentRepository.save(comment);
        return commentMapper.toDto(saved);
    }

    public List<CommentResponseDto> findAllByTask(Long taskId) {
        Task task = taskService.getTask(taskId);

        AppUser currentUser = authorizationService.getCurrentUser();

        List<Comment> comments = commentRepository.findAllByTask(task);

        return comments.stream()
                .map(commentMapper::toDto).toList();

    }

    @Transactional
    public void update(CommentUpdateDto commentUpdateDto, Long commentId) {
        Comment comment = getComment(commentId);

        authorizationService.validateSelf(comment.getAppUser().getUserid());

        commentMapper.update(commentUpdateDto, comment);
    }

    @Transactional
    public void delete(Long commentId) {

        Comment comment = getComment(commentId);
        authorizationService.validateSelfAdminOrProjectManager(comment.getAppUser().getUserid(), comment.getTask().getProject().getProjectId());

        commentRepository.delete(comment);

    }

    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
    }


}
