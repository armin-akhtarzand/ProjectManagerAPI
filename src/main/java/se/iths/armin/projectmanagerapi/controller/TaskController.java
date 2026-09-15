package se.iths.armin.projectmanagerapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.iths.armin.projectmanagerapi.dto.task.*;
import se.iths.armin.projectmanagerapi.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;


    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllProjectTasks(@PathVariable Long projectId) {
        List<TaskResponseDto> responseDto = taskService.findAllProjectTasks(projectId);


        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponseDto> getTask(@PathVariable Long projectId,
                                                   @PathVariable Long taskId) {
        TaskResponseDto responseDto = taskService.findTaskById(taskId, projectId);


        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping()
    public ResponseEntity<TaskResponseDto> createTask(@RequestBody @Valid TaskRequestDto taskRequestDto,
                                                      @PathVariable Long projectId) {
        TaskResponseDto responseDto = taskService.createTask(taskRequestDto, projectId);


        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDto> updateTask(@RequestBody @Valid TaskUpdateDto updateDto,
                                                      @PathVariable Long projectId,
                                                      @PathVariable Long taskId) {
        TaskResponseDto responseDto = taskService.updateTask(taskId, projectId, updateDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<Void> changeTaskStatus(@RequestBody @Valid ChangeTaskStatusDto changeStatus,
                                                 @PathVariable Long projectId,
                                                 @PathVariable Long taskId) {
        taskService.changeTaskStatus(taskId, projectId, changeStatus);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{taskId}/assignee")
    public ResponseEntity<Void> changeTaskAssignee(@RequestBody @Valid ChangeTaskAssigneeDto changeAssignee,
                                                   @PathVariable Long projectId,
                                                   @PathVariable Long taskId) {
        taskService.changeTaskAssignee(changeAssignee, projectId, taskId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long projectId, @PathVariable Long taskId) {
        taskService.deleteTask(taskId, projectId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
