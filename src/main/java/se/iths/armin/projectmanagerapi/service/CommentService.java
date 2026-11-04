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
    private final TaskService taskService;
    private final AuthorizationService authorizationService;


    @Transactional
    public CommentResponseDto create(CommentRequestDto commentRequestDto, Long taskId, Long projectId) {
        Project project = projectService.getProject(projectId);
        AppUser from = authorizationService.getCurrentUser();
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

    public List<CommentResponseDto> findAllByTask(Long taskId, Long projectId) {
        Task task = taskService.getTask(taskId);

        if (!task.getProject().getProjectId().equals(projectId)) {
            throw new ResourceNotFoundException("Task does not belong to project");
        }

        List<Comment> comments = commentRepository.findAllByTask(task);

        return comments.stream()
                .map(commentMapper::toDto).toList();

    }

    @Transactional
    public void update(CommentUpdateDto commentUpdateDto, Long commentId, Long projectId, Long taskId) {
        Comment comment = getComment(commentId);

        if (!comment.getTask().getTaskId().equals(taskId)) {
            throw new ResourceNotFoundException("Comment does not belong to task");
        }
        if (!comment.getTask().getProject().getProjectId().equals(projectId)) {
            throw new ResourceNotFoundException("Task does not belong to project");
        }

        authorizationService.validateSelf(comment.getAppUser().getUserid());


        commentMapper.update(commentUpdateDto, comment);
    }

    @Transactional
    public void delete(Long commentId, Long taskId, Long projectId) {

        Comment comment = getComment(commentId);

        if (!comment.getTask().getTaskId().equals(taskId)) {
            throw new ResourceNotFoundException("Comment does not belong to task");
        }
        if (!comment.getTask().getProject().getProjectId().equals(projectId)) {
            throw new ResourceNotFoundException("Task does not belong to project");
        }
        authorizationService.validateSelfAdminOrProjectManager(comment.getAppUser().getUserid(), comment.getTask().getProject().getProjectId());

        commentRepository.delete(comment);

    }

    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
    }


}
