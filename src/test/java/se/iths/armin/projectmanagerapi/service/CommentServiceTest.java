package se.iths.armin.projectmanagerapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.iths.armin.projectmanagerapi.dto.comment.CommentRequestDto;
import se.iths.armin.projectmanagerapi.dto.comment.CommentResponseDto;
import se.iths.armin.projectmanagerapi.dto.comment.CommentUpdateDto;
import se.iths.armin.projectmanagerapi.entity.AppUser;
import se.iths.armin.projectmanagerapi.entity.Comment;
import se.iths.armin.projectmanagerapi.entity.Project;
import se.iths.armin.projectmanagerapi.entity.Task;
import se.iths.armin.projectmanagerapi.exception.ForbiddenRequestException;
import se.iths.armin.projectmanagerapi.exception.ResourceNotFoundException;
import se.iths.armin.projectmanagerapi.mapper.CommentMapperImpl;
import se.iths.armin.projectmanagerapi.repository.CommentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapperImpl commentMapper;
    @Mock
    private ProjectUserService projectUserService;
    @Mock
    private ProjectService projectService;
    @Mock
    private TaskService taskService;
    @Mock
    private AuthorizationService authorizationService;
    @InjectMocks
    private CommentService commentService;

    private Project project;
    private Task task;
    private AppUser user;
    private Comment comment;
    private CommentRequestDto requestDto;
    private CommentResponseDto responseDto;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setProjectId(1L);
        project.setTitle("Test Project");

        task = new Task();
        task.setTaskId(10L);
        task.setProject(project);

        user = new AppUser();
        user.setUserid(2L);
        user.setFirstname("FirstName");
        user.setLastname("LastName");

        requestDto = new CommentRequestDto("Test comment");
        comment = new Comment();
        comment.setCommentId(100L);
        comment.setContent(requestDto.content());
        comment.setTask(task);
        comment.setAppUser(user);
        comment.setCreatedAt(LocalDateTime.of(2030, 1, 15, 12, 0));

        responseDto = new CommentResponseDto(
                comment.getContent(), user.getUserid(), user.getFirstname(),
                user.getLastname(), task.getTaskId(), comment.getCreatedAt());
    }

    @Test
    void create_WhenValidRequest_ShouldReturnCommentResponseDto() {
        when(projectService.getProject(1L)).thenReturn(project);
        when(authorizationService.getCurrentUser()).thenReturn(user);
        when(taskService.getTask(10L)).thenReturn(task);
        Comment newComment = new Comment();
        newComment.setContent(requestDto.content());
        when(commentMapper.toEntity(requestDto)).thenReturn(newComment);
        when(commentRepository.save(newComment)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(responseDto);

        CommentResponseDto result = commentService.create(requestDto, 10L, 1L);

        assertEquals(responseDto, result);
        assertSame(user, newComment.getAppUser());
        assertSame(task, newComment.getTask());
        verify(authorizationService).getCurrentUser();
        verify(projectUserService).getProjectUser(user, project);
        verify(commentRepository).save(newComment);
        verify(commentMapper).toDto(comment);
    }

    @Test
    void create_WhenProjectNotFound_ShouldThrowException() {
        when(projectService.getProject(1L)).thenThrow(new ResourceNotFoundException("Project not found"));

        assertThrows(ResourceNotFoundException.class, () -> commentService.create(requestDto, 10L, 1L));

        verify(commentRepository, never()).save(any(Comment.class));
        verifyNoInteractions(commentMapper, taskService);
    }

    @Test
    void create_WhenTaskNotFound_ShouldThrowException() {
        when(projectService.getProject(1L)).thenReturn(project);
        when(authorizationService.getCurrentUser()).thenReturn(user);
        when(taskService.getTask(10L)).thenThrow(new ResourceNotFoundException("Task not found"));

        assertThrows(ResourceNotFoundException.class, () -> commentService.create(requestDto, 10L, 1L));

        verify(commentRepository, never()).save(any(Comment.class));
        verifyNoInteractions(projectUserService, commentMapper);
    }

    @Test
    void create_WhenUserNotProjectMember_ShouldThrowException() {
        when(projectService.getProject(1L)).thenReturn(project);
        when(authorizationService.getCurrentUser()).thenReturn(user);
        when(taskService.getTask(10L)).thenReturn(task);
        when(projectUserService.getProjectUser(user, project))
                .thenThrow(new ResourceNotFoundException("ProjectUser not found"));

        assertThrows(ResourceNotFoundException.class, () -> commentService.create(requestDto, 10L, 1L));

        verify(commentRepository, never()).save(any(Comment.class));
        verifyNoInteractions(commentMapper);
    }

    @Test
    void create_WhenTaskBelongsToAnotherProject_ShouldThrowException() {
        when(projectService.getProject(1L)).thenReturn(project);
        when(authorizationService.getCurrentUser()).thenReturn(user);
        when(taskService.getTask(10L)).thenReturn(task);
        Project otherProject = new Project();
        otherProject.setProjectId(3L);
        task.setProject(otherProject);

        assertThrows(ResourceNotFoundException.class, () -> commentService.create(requestDto, 10L, 1L));

        verify(commentRepository, never()).save(any(Comment.class));
        verifyNoInteractions(commentMapper);
    }

    @Test
    void findAllByTask_WhenValidRequest_ShouldReturnComments() {
        when(taskService.getTask(10L)).thenReturn(task);
        when(commentRepository.findAllByTask(task)).thenReturn(List.of(comment));
        when(commentMapper.toDto(comment)).thenReturn(responseDto);

        List<CommentResponseDto> result = commentService.findAllByTask(10L, 1L);

        assertEquals(List.of(responseDto), result);
        verify(commentRepository).findAllByTask(task);
        verify(commentMapper).toDto(comment);
    }

    @Test
    void findAllByTask_WhenNoComments_ShouldReturnEmptyList() {
        when(taskService.getTask(10L)).thenReturn(task);
        when(commentRepository.findAllByTask(task)).thenReturn(List.of());

        List<CommentResponseDto> result = commentService.findAllByTask(10L, 1L);

        assertTrue(result.isEmpty());
        verify(commentRepository).findAllByTask(task);
        verifyNoInteractions(commentMapper);
    }

    @Test
    void findAllByTask_WhenTaskNotFound_ShouldThrowException() {
        when(taskService.getTask(10L)).thenThrow(new ResourceNotFoundException("Task not found"));

        assertThrows(ResourceNotFoundException.class, () -> commentService.findAllByTask(10L, 1L));

        verifyNoInteractions(commentRepository, commentMapper);
    }

    @Test
    void findAllByTask_WhenTaskBelongsToAnotherProject_ShouldThrowException() {
        when(taskService.getTask(10L)).thenReturn(task);

        assertThrows(ResourceNotFoundException.class, () -> commentService.findAllByTask(10L, 3L));

        verifyNoInteractions(commentRepository, commentMapper);
    }

    @Test
    void getComment_WhenCommentExists_ShouldReturnComment() {
        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

        Comment result = commentService.getComment(100L);

        assertSame(comment, result);
        verify(commentRepository).findById(100L);
    }

    @Test
    void getComment_WhenCommentNotFound_ShouldThrowException() {
        when(commentRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.getComment(100L));

        verify(commentRepository).findById(100L);
    }

    @Test
    void update_WhenValidRequest_ShouldMapUpdatedContent() {
        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));
        CommentUpdateDto updateDto = new CommentUpdateDto("Updated comment");

        commentService.update(updateDto, 100L, 10L, 1L);

        verify(authorizationService).validateSelf(2L);
        verify(commentMapper).update(updateDto, comment);
    }

    @Test
    void update_WhenCommentNotFound_ShouldThrowException() {
        when(commentRepository.findById(100L)).thenReturn(Optional.empty());
        CommentUpdateDto updateDto = new CommentUpdateDto("Updated comment");

        assertThrows(ResourceNotFoundException.class, ()
                -> commentService.update(updateDto, 100L, 10L, 1L));

        verifyNoInteractions(commentMapper, authorizationService);
    }

    @Test
    void delete_WhenCommentNotFound_ShouldThrowException() {
        when(commentRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, ()
                -> commentService.delete(100L, 10L, 1L));

        verify(commentRepository, never()).delete(any(Comment.class));
        verifyNoInteractions(authorizationService);
    }

    @Test
    void update_WhenCommentBelongsToAnotherTask_ShouldThrowException() {
        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));
        CommentUpdateDto updateDto = new CommentUpdateDto("Updated comment");

        assertThrows(ResourceNotFoundException.class, ()
                -> commentService.update(updateDto, 100L, 11L, 1L));

        verifyNoInteractions(commentMapper, authorizationService);
    }

    @Test
    void delete_WhenCommentBelongsToAnotherTask_ShouldThrowException() {
        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

        assertThrows(ResourceNotFoundException.class, ()
                -> commentService.delete(100L, 11L, 1L));

        verify(commentRepository, never()).delete(any(Comment.class));
        verifyNoInteractions(authorizationService);
    }

    @Test
    void update_WhenTaskBelongsToAnotherProject_ShouldThrowException() {
        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));
        CommentUpdateDto updateDto = new CommentUpdateDto("Updated comment");

        assertThrows(ResourceNotFoundException.class, ()
                -> commentService.update(updateDto, 100L, 10L, 3L));

        verifyNoInteractions(commentMapper, authorizationService);
    }

    @Test
    void delete_WhenTaskBelongsToAnotherProject_ShouldThrowException() {
        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

        assertThrows(ResourceNotFoundException.class, ()
                -> commentService.delete(100L, 10L, 3L));

        verify(commentRepository, never()).delete(any(Comment.class));
        verifyNoInteractions(authorizationService);
    }

    @Test
    void update_WhenUnauthorized_ShouldNotUpdateComment() {
        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));
        CommentUpdateDto updateDto = new CommentUpdateDto("Updated comment");
        doThrow(new ForbiddenRequestException("Not allowed"))
                .when(authorizationService).validateSelf(2L);

        assertThrows(ForbiddenRequestException.class, ()
                -> commentService.update(updateDto, 100L, 10L, 1L));

        verifyNoInteractions(commentMapper);
        assertEquals("Test comment", comment.getContent());
    }

    @Test
    void delete_WhenValidRequest_ShouldDeleteComment() {
        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

        commentService.delete(100L, 10L, 1L);

        verify(authorizationService).validateSelfAdminOrProjectManager(2L, 1L);
        verify(commentRepository).delete(comment);
    }

    @Test
    void delete_WhenUnauthorized_ShouldNotDeleteComment() {
        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));
        doThrow(new ForbiddenRequestException("Not allowed"))
                .when(authorizationService).validateSelfAdminOrProjectManager(2L, 1L);

        assertThrows(ForbiddenRequestException.class, () -> commentService.delete(100L, 10L, 1L));

        verify(commentRepository, never()).delete(any(Comment.class));
    }
}
