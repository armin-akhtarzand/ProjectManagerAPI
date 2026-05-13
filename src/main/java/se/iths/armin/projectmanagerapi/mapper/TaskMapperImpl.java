package se.iths.armin.projectmanagerapi.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.iths.armin.projectmanagerapi.dto.task.TaskRequestDto;
import se.iths.armin.projectmanagerapi.dto.task.TaskResponseDto;
import se.iths.armin.projectmanagerapi.dto.task.TaskUpdateDto;
import se.iths.armin.projectmanagerapi.entity.AppUser;
import se.iths.armin.projectmanagerapi.entity.Project;
import se.iths.armin.projectmanagerapi.entity.Task;
import se.iths.armin.projectmanagerapi.service.AppUserService;
import se.iths.armin.projectmanagerapi.service.ProjectService;

@Component
@RequiredArgsConstructor
public class TaskMapperImpl
        implements EntityMapper<Task, TaskRequestDto, TaskResponseDto> {

    private final ProjectService projectService;
    private final AppUserService appUserService;

    @Override
    public Task toEntity(TaskRequestDto taskRequestDto) {
        if (taskRequestDto == null) {
            return null;
        }

        AppUser createdBy = appUserService.getAppUser(taskRequestDto.creatorId());
        AppUser assignee = appUserService.getAppUser(taskRequestDto.assigneeId());
        Project project = projectService.getProject(taskRequestDto.projectId());

        Task task = new Task();
        task.setCreatedBy(createdBy);
        task.setAssignee(assignee);
        task.setProject(project);
        task.setDescription(taskRequestDto.description());
        task.setTitle(taskRequestDto.title());
        task.setDeadline(taskRequestDto.deadline());


        return task;
    }

    @Override
    public TaskResponseDto toDto(Task task) {
        if (task == null) {
            return null;
        }
        AppUser createdBy = task.getCreatedBy();
        AppUser assignee = task.getAssignee();
        Project project = task.getProject();
        TaskResponseDto taskResponseDto = new TaskResponseDto(
                createdBy.getUserid(), createdBy.getFirstname(), assignee.getUserid(), assignee.getFirstname(), assignee.getLastname(),
                project.getProjectId(), project.getTitle(), task.getTitle(), task.getDescription(), task.getTaskStatus(), task.getDeadline()
        );

        return taskResponseDto;
    }

    public void update(Task task, TaskUpdateDto taskUpdateDto) {

        if (task == null) {
            return;
        }
        task.setDescription(taskUpdateDto.description());
        task.setTitle(taskUpdateDto.title());
        task.setDeadline(taskUpdateDto.deadline());
    }

}
