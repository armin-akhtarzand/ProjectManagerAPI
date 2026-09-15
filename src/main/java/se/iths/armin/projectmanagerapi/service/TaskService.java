package se.iths.armin.projectmanagerapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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


    @Transactional
    public TaskResponseDto createTask(TaskRequestDto taskRequestDto, Long projectId) {

        authorizationService.validateProjectManagerOrAdmin(projectId);
        Project project = projectService.getProject(projectId);
        AppUser currentUser = authorizationService.getCurrentUser();

        Task task = taskMapper.toEntity(taskRequestDto);
        task.setProject(project);
        task.setCreatedBy(currentUser);
        boolean isMember = projectUserRepository.existsByAppUserAndProject(task.getAssignee(), project);
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

    public TaskResponseDto findTaskById(Long taskId, Long projectId) {
        Task task = getTaskFromProject(projectId, taskId);

        return taskMapper.toDto(task);
    }

    public List<TaskResponseDto> findAllProjectTasks(Long projectId) {
        Project project = projectService.getProject(projectId);

        List<Task> projectTasks = taskRepository.findAllByProject(project);

        return projectTasks.stream().map(taskMapper::toDto).toList();
    }

    @Transactional
    public TaskResponseDto updateTask(Long taskId, Long projectId, TaskUpdateDto taskUpdateDto) {
        Task task = getTaskFromProject(projectId, taskId);
        authorizationService.validateProjectManagerOrAdmin(task.getProject().getProjectId());

        taskMapper.update(task, taskUpdateDto);

        return taskMapper.toDto(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long taskId, Long projectId) {

        Task task = getTaskFromProject(projectId, taskId);
        authorizationService.validateProjectManagerOrAdmin(task.getProject().getProjectId());

        taskRepository.delete(task);
    }

    @Transactional
    public void changeTaskStatus(Long taskId, Long projectId,
                                 ChangeTaskStatusDto changeTaskStatusDto) {

        Task task = getTaskFromProject(projectId, taskId);
        authorizationService.validateSelfAdminOrProjectManager(task.getAssignee().getUserid(), task.getProject().getProjectId());

        if (task.getTaskStatus().equals(changeTaskStatusDto.status())) {
            throw new NoStateChangeException("Task already has this status");
        }
        task.setTaskStatus(changeTaskStatusDto.status());
        taskRepository.save(task);
    }

    @Transactional
    public void changeTaskAssignee(ChangeTaskAssigneeDto changeTaskAssigneeDto, Long projectId, Long taskId) {
        Task task = getTaskFromProject(projectId, taskId);
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

    private Task getTaskFromProject(Long projectId, Long taskId) {
        Task task = getTask(taskId);

        if (!task.getProject().getProjectId().equals(projectId)) {
            throw new ResourceNotFoundException("Task does not belong to this project");
        }

        return task;
    }


}
