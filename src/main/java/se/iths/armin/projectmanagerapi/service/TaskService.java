package se.iths.armin.projectmanagerapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.iths.armin.projectmanagerapi.dto.task.*;
import se.iths.armin.projectmanagerapi.entity.AppUser;
import se.iths.armin.projectmanagerapi.entity.Project;
import se.iths.armin.projectmanagerapi.entity.Task;
import se.iths.armin.projectmanagerapi.exception.NoStateChangeException;
import se.iths.armin.projectmanagerapi.exception.ResourceNotFoundException;
import se.iths.armin.projectmanagerapi.mapper.TaskMapperImpl;
import se.iths.armin.projectmanagerapi.repository.ProjectUserRepository;
import se.iths.armin.projectmanagerapi.repository.TaskRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapperImpl taskMapper;
    private final AuthorizationService authorizationService;
    private final ProjectService projectService;
    private final AppUserService appUserService;
    private final ProjectUserRepository projectUserRepository;


    public TaskResponseDto createTask(TaskRequestDto taskRequestDto, Long projectId) {

        authorizationService.validateProjectManagerOrAdmin(projectId);

        Task task = taskMapper.toEntity(taskRequestDto);
        boolean isMember = projectUserRepository.existsByAppUserAndProject(task.getAssignee(), task.getProject());
        if (!isMember) {
            throw new ResourceNotFoundException("Assignee is not a member of project");
        }

        Task saved = taskRepository.save(task);

        return taskMapper.toDto(saved);
    }

    public Task getTask(Long taskId) {

        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    public TaskResponseDto findTaskById(Long taskId) {
        Task task = getTask(taskId);

        return taskMapper.toDto(task);
    }

    public List<TaskResponseDto> findAllProjectTasks(Long projectId) {
        Project project = projectService.getProject(projectId);

        List<Task> projectTasks = taskRepository.findAllByProject(project);

        return projectTasks.stream().map(taskMapper::toDto).toList();
    }

    public TaskResponseDto updateTask(Long taskId, TaskUpdateDto taskUpdateDto) {
        Task task = getTask(taskId);
        authorizationService.validateProjectManagerOrAdmin(task.getProject().getProjectId());

        taskMapper.update(task, taskUpdateDto);

        return taskMapper.toDto(taskRepository.save(task));
    }

    public void deleteTask(Long taskId) {

        Task task = getTask(taskId);
        authorizationService.validateProjectManagerOrAdmin(task.getProject().getProjectId());

        taskRepository.delete(task);
    }

    public void updateTaskStatus(Long userId, Long taskId,
                                 ChangeTaskStatusDto changeTaskStatusDto) {

        Task task = getTask(taskId);
        authorizationService.validateSelfAdminOrProjectManager(userId, task.getProject().getProjectId());

        if (task.getTaskStatus().equals(changeTaskStatusDto.status())) {
            throw new NoStateChangeException("Task already has this status");
        }
        task.setTaskStatus(changeTaskStatusDto.status());
        taskRepository.save(task);
    }

    public void changeTaskAssignee(ChangeTaskAssigneeDto changeTaskAssigneeDto, Long taskId) {
        Task task = getTask(taskId);
        authorizationService.validateProjectManagerOrAdmin(task.getProject().getProjectId());

        AppUser newAssignee = appUserService.getAppUser(changeTaskAssigneeDto.assigneeId());

        boolean isMember = projectUserRepository.existsByAppUserAndProject(newAssignee, task.getProject());

        if (!isMember) {
            throw new ResourceNotFoundException("Assignee is not a member of project");
        }
        if (task.getAssignee().getUserid().equals(newAssignee.getUserid())) {
            throw new NoStateChangeException("Current assignee has already been assigned");
        }

        task.setAssignee(newAssignee);
        taskRepository.save(task);
    }


}
