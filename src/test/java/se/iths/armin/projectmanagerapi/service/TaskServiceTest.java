package se.iths.armin.projectmanagerapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.iths.armin.projectmanagerapi.dto.task.ChangeTaskAssigneeDto;
import se.iths.armin.projectmanagerapi.dto.task.ChangeTaskStatusDto;
import se.iths.armin.projectmanagerapi.dto.task.TaskUpdateDto;
import se.iths.armin.projectmanagerapi.dto.task.TaskRequestDto;
import se.iths.armin.projectmanagerapi.dto.task.TaskResponseDto;
import se.iths.armin.projectmanagerapi.entity.AppUser;
import se.iths.armin.projectmanagerapi.entity.Project;
import se.iths.armin.projectmanagerapi.entity.Task;
import se.iths.armin.projectmanagerapi.entity.enums.TaskStatus;
import se.iths.armin.projectmanagerapi.exception.ForbiddenRequestException;
import se.iths.armin.projectmanagerapi.exception.NoStateChangeException;
import se.iths.armin.projectmanagerapi.exception.ResourceNotFoundException;
import se.iths.armin.projectmanagerapi.mapper.TaskMapperImpl;
import se.iths.armin.projectmanagerapi.repository.ProjectUserRepository;
import se.iths.armin.projectmanagerapi.repository.TaskRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private AuthorizationService authorizationService;
    @Mock
    private TaskMapperImpl taskMapperImpl;
    @Mock
    private AppUserService appUserService;
    @Mock
    private ProjectService projectService;
    @Mock
    private ProjectUserRepository projectUserRepository;
    @InjectMocks
    private TaskService taskService;

    private Project project;
    private AppUser user;
    private Task task;
    private TaskResponseDto responseDto;
    private TaskRequestDto requestDto;


    @BeforeEach
    void setUp() {
        user = new AppUser();
        project = new Project();
        project.setProjectId(1L);
        project.setDescription("Test Project");
        project.setTitle("Test Project");
        user.setUserid(1L);
        user.setFirstname("FirstName");
        user.setLastname("LastName");
        LocalDate deadline = LocalDate.of(2030, 1, 15);


        requestDto = new TaskRequestDto(1L, "TestTitle", "TestDescription", deadline);

        task = new Task();
        task.setTaskId(10L);
        task.setCreatedBy(user);
        task.setAssignee(user);
        task.setProject(project);
        task.setDescription(requestDto.description());
        task.setTitle(requestDto.title());
        task.setDeadline(deadline);

        responseDto = new TaskResponseDto(
                user.getUserid(), user.getFirstname(),
                user.getUserid(), user.getFirstname(), user.getLastname(),
                project.getProjectId(), project.getTitle(),
                task.getTitle(), task.getDescription(), task.getTaskStatus(), deadline
        );
    }

    @Test
    void createTask_WhenValidRequest_ShouldReturnTaskResponseDto() {
        task.setProject(null);
        task.setCreatedBy(null);
        when(projectService.getProject(1L)).thenReturn(project);
        when(authorizationService.getCurrentUser()).thenReturn(user);
        when(taskMapperImpl.toEntity(requestDto)).thenReturn(task);
        when(projectUserRepository.existsByAppUserAndProject(user, project)).thenReturn(true);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapperImpl.toDto(task)).thenReturn(responseDto);

        TaskResponseDto result = taskService.createTask(requestDto, 1L);

        assertEquals(responseDto, result);
        assertEquals(requestDto.title(), result.title());
        assertEquals(project, task.getProject());
        assertEquals(user, task.getCreatedBy());
        verify(authorizationService).validateProjectManagerOrAdmin(1L);
        verify(projectUserRepository).existsByAppUserAndProject(user, project);
        verify(taskRepository).save(task);
    }

    @Test
    void createTask_WhenAssigneeNotProjectMember_ShouldThrowException() {
        when(projectService.getProject(1L)).thenReturn(project);
        when(authorizationService.getCurrentUser()).thenReturn(user);
        when(taskMapperImpl.toEntity(requestDto)).thenReturn(task);
        when(projectUserRepository.existsByAppUserAndProject(user, project)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, ()
                -> taskService.createTask(requestDto, 1L));

        verify(authorizationService).validateProjectManagerOrAdmin(1L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void createTask_WhenProjectNotFound_ShouldThrowException() {
        when(projectService.getProject(2L)).thenThrow(new ResourceNotFoundException("Project not found"));

        assertThrows(ResourceNotFoundException.class, ()
                -> taskService.createTask(requestDto, 2L));

        verify(taskRepository, never()).save(any(Task.class));
        verifyNoInteractions(taskMapperImpl);
    }

    @Test
    void createTask_WhenUnauthorized_ShouldThrowException() {
        doThrow(new ForbiddenRequestException("Not allowed"))
                .when(authorizationService).validateProjectManagerOrAdmin(1L);

        assertThrows(ForbiddenRequestException.class, ()
                -> taskService.createTask(requestDto, 1L));

        verifyNoInteractions(projectService, taskMapperImpl, taskRepository);
    }

    @Test
    void getTask_WhenTaskExists_ShouldReturnTask() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        Task result = taskService.getTask(10L);

        assertSame(task, result);
        verify(taskRepository).findById(10L);
    }

    @Test
    void getTask_WhenTaskNotFound_ShouldThrowException() {
        when(taskRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTask(10L));

        verify(taskRepository).findById(10L);
    }

    @Test
    void findTaskById_WhenValidRequest_ShouldReturnTaskResponseDto() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(taskMapperImpl.toDto(task)).thenReturn(responseDto);

        TaskResponseDto result = taskService.findTaskById(10L, 1L);

        assertEquals(responseDto, result);
        verify(taskMapperImpl).toDto(task);
    }

    @Test
    void findTaskById_WhenTaskNotFound_ShouldThrowException() {
        when(taskRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.findTaskById(10L, 1L));

        verifyNoInteractions(taskMapperImpl);
    }

    @Test
    void findTaskById_WhenTaskBelongsToAnotherProject_ShouldThrowException() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        assertThrows(ResourceNotFoundException.class, () -> taskService.findTaskById(10L, 2L));

        verifyNoInteractions(taskMapperImpl);
    }

    @Test
    void findAllProjectTasks_WhenValidRequest_ShouldReturnTaskResponseDtos() {
        when(projectService.getProject(1L)).thenReturn(project);
        when(taskRepository.findAllByProject(project)).thenReturn(List.of(task));
        when(taskMapperImpl.toDto(task)).thenReturn(responseDto);

        List<TaskResponseDto> result = taskService.findAllProjectTasks(1L);

        assertEquals(1, result.size());
        assertEquals(responseDto, result.get(0));
        verify(taskRepository).findAllByProject(project);
    }

    @Test
    void findAllProjectTasks_WhenNoTasks_ShouldReturnEmptyList() {
        when(projectService.getProject(1L)).thenReturn(project);
        when(taskRepository.findAllByProject(project)).thenReturn(List.of());

        List<TaskResponseDto> result = taskService.findAllProjectTasks(1L);

        assertTrue(result.isEmpty());
        verifyNoInteractions(taskMapperImpl);
    }

    @Test
    void findAllProjectTasks_WhenProjectNotFound_ShouldThrowException() {
        when(projectService.getProject(2L)).thenThrow(new ResourceNotFoundException("Project not found"));

        assertThrows(ResourceNotFoundException.class, () -> taskService.findAllProjectTasks(2L));

        verify(taskRepository, never()).findAllByProject(any(Project.class));
    }

    @Test
    void updateTask_WhenValidRequest_ShouldReturnUpdatedTaskResponseDto() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        TaskUpdateDto updateDto = new TaskUpdateDto("Updated title", "Updated description", task.getDeadline());
        TaskResponseDto updatedResponse = new TaskResponseDto(
                user.getUserid(), user.getFirstname(), user.getUserid(), user.getFirstname(), user.getLastname(),
                1L, project.getTitle(), updateDto.title(), updateDto.description(), task.getTaskStatus(), updateDto.deadline());
        doAnswer(invocation -> {
            task.setTitle(updateDto.title());
            task.setDescription(updateDto.description());
            task.setDeadline(updateDto.deadline());
            return null;
        }).when(taskMapperImpl).update(task, updateDto);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapperImpl.toDto(task)).thenReturn(updatedResponse);

        TaskResponseDto result = taskService.updateTask(10L, 1L, updateDto);

        assertEquals(updatedResponse, result);
        assertEquals(updateDto.title(), task.getTitle());
        verify(authorizationService).validateProjectManagerOrAdmin(1L);
        verify(taskMapperImpl).update(task, updateDto);
        verify(taskRepository).save(task);
    }

    @Test
    void updateTask_WhenTaskNotFound_ShouldThrowException() {
        when(taskRepository.findById(10L)).thenReturn(Optional.empty());
        TaskUpdateDto updateDto = new TaskUpdateDto("Updated", "Updated", task.getDeadline());

        assertThrows(ResourceNotFoundException.class, () -> taskService.updateTask(10L, 1L, updateDto));

        verify(taskRepository, never()).save(any(Task.class));
        verifyNoInteractions(taskMapperImpl);
    }

    @Test
    void deleteTask_WhenTaskNotFound_ShouldThrowException() {
        when(taskRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(10L, 1L));

        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    void changeTaskStatus_WhenTaskNotFound_ShouldThrowException() {
        when(taskRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, ()
                -> taskService.changeTaskStatus(10L, 1L, new ChangeTaskStatusDto(TaskStatus.DONE)));

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void changeTaskAssignee_WhenTaskNotFound_ShouldThrowException() {
        when(taskRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, ()
                -> taskService.changeTaskAssignee(new ChangeTaskAssigneeDto(2L), 1L, 10L));

        verify(taskRepository, never()).save(any(Task.class));
        verifyNoInteractions(appUserService);
    }

    @Test
    void updateTask_WhenTaskBelongsToAnotherProject_ShouldThrowException() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        TaskUpdateDto updateDto = new TaskUpdateDto("Updated", "Updated", task.getDeadline());

        assertThrows(ResourceNotFoundException.class, () -> taskService.updateTask(10L, 2L, updateDto));

        verify(taskRepository, never()).save(any(Task.class));
        verifyNoInteractions(taskMapperImpl);
    }

    @Test
    void deleteTask_WhenTaskBelongsToAnotherProject_ShouldThrowException() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(10L, 2L));

        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    void changeTaskStatus_WhenTaskBelongsToAnotherProject_ShouldThrowException() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        assertThrows(ResourceNotFoundException.class, ()
                -> taskService.changeTaskStatus(10L, 2L, new ChangeTaskStatusDto(TaskStatus.DONE)));

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void changeTaskAssignee_WhenTaskBelongsToAnotherProject_ShouldThrowException() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        assertThrows(ResourceNotFoundException.class, ()
                -> taskService.changeTaskAssignee(new ChangeTaskAssigneeDto(2L), 2L, 10L));

        verify(taskRepository, never()).save(any(Task.class));
        verifyNoInteractions(appUserService);
    }

    @Test
    void deleteTask_WhenValidRequest_ShouldDeleteTask() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        taskService.deleteTask(10L, 1L);

        verify(authorizationService).validateProjectManagerOrAdmin(1L);
        verify(taskRepository).delete(task);
    }

    @Test
    void changeTaskStatus_WhenValidRequest_ShouldSaveUpdatedStatus() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        taskService.changeTaskStatus(10L, 1L, new ChangeTaskStatusDto(TaskStatus.DONE));

        assertEquals(TaskStatus.DONE, task.getTaskStatus());
        verify(authorizationService).validateSelfAdminOrProjectManager(1L, 1L);
        verify(taskRepository).save(task);
    }

    @Test
    void changeTaskStatus_WhenNoStateChange_ShouldThrowException() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        assertThrows(NoStateChangeException.class, ()
                -> taskService.changeTaskStatus(10L, 1L, new ChangeTaskStatusDto(TaskStatus.TODO)));

        verify(authorizationService).validateSelfAdminOrProjectManager(1L, 1L);
        assertEquals(TaskStatus.TODO, task.getTaskStatus());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void changeTaskStatus_WhenUnauthorized_ShouldNotChangeStatus() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        doThrow(new ForbiddenRequestException("Not allowed"))
                .when(authorizationService).validateSelfAdminOrProjectManager(1L, 1L);

        assertThrows(ForbiddenRequestException.class, ()
                -> taskService.changeTaskStatus(10L, 1L, new ChangeTaskStatusDto(TaskStatus.DONE)));

        assertEquals(TaskStatus.TODO, task.getTaskStatus());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void changeTaskAssignee_WhenValidRequest_ShouldSaveNewAssignee() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        AppUser newAssignee = new AppUser();
        newAssignee.setUserid(2L);
        when(appUserService.getAppUser(2L)).thenReturn(newAssignee);
        when(projectUserRepository.existsByAppUserAndProject(newAssignee, project)).thenReturn(true);

        taskService.changeTaskAssignee(new ChangeTaskAssigneeDto(2L), 1L, 10L);

        assertSame(newAssignee, task.getAssignee());
        verify(authorizationService).validateProjectManagerOrAdmin(1L);
        verify(taskRepository).save(task);
    }

    @Test
    void changeTaskAssignee_WhenAssigneeNotProjectMember_ShouldThrowException() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        AppUser newAssignee = new AppUser();
        newAssignee.setUserid(2L);
        when(appUserService.getAppUser(2L)).thenReturn(newAssignee);
        when(projectUserRepository.existsByAppUserAndProject(newAssignee, project)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, ()
                -> taskService.changeTaskAssignee(new ChangeTaskAssigneeDto(2L), 1L, 10L));

        assertSame(user, task.getAssignee());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void changeTaskAssignee_WhenNoStateChange_ShouldThrowException() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(appUserService.getAppUser(1L)).thenReturn(user);
        when(projectUserRepository.existsByAppUserAndProject(user, project)).thenReturn(true);

        assertThrows(NoStateChangeException.class, ()
                -> taskService.changeTaskAssignee(new ChangeTaskAssigneeDto(1L), 1L, 10L));

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void changeTaskAssignee_WhenUserNotFound_ShouldThrowException() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(appUserService.getAppUser(2L)).thenThrow(new ResourceNotFoundException("User not found"));

        assertThrows(ResourceNotFoundException.class, ()
                -> taskService.changeTaskAssignee(new ChangeTaskAssigneeDto(2L), 1L, 10L));

        verifyNoInteractions(projectUserRepository);
        verify(taskRepository, never()).save(any(Task.class));
    }
}
