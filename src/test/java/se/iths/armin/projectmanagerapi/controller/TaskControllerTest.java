package se.iths.armin.projectmanagerapi.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import se.iths.armin.projectmanagerapi.dto.task.*;
import se.iths.armin.projectmanagerapi.entity.enums.TaskStatus;
import se.iths.armin.projectmanagerapi.exception.ForbiddenRequestException;
import se.iths.armin.projectmanagerapi.exception.NoStateChangeException;
import se.iths.armin.projectmanagerapi.exception.ResourceNotFoundException;
import se.iths.armin.projectmanagerapi.service.TaskService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@ActiveProfiles("test")
public class TaskControllerTest {

    @MockitoBean
    private TaskService taskService;
    @Autowired
    private MockMvc mockMvc;

    private TaskResponseDto responseDto;

    @BeforeEach
    void setUp() {
        responseDto = new TaskResponseDto(
                1L, "Creator", 2L, "FirstName", "LastName",
                1L, "Test Project", "Test Task", "Test description",
                TaskStatus.TODO, LocalDate.of(2030, 1, 15)
        );
    }

    @Test
    void getAllProjectTasks_WhenValidRequest_ShouldReturn200() throws Exception {
        when(taskService.findAllProjectTasks(1L)).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/projects/1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Task"))
                .andExpect(jsonPath("$[0].projectId").value(1));

        verify(taskService).findAllProjectTasks(1L);
    }

    @Test
    void getAllProjectTasks_WhenNoTasks_ShouldReturnEmptyList() throws Exception {
        when(taskService.findAllProjectTasks(1L)).thenReturn(List.of());

        mockMvc.perform(get("/projects/1/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(taskService).findAllProjectTasks(1L);
    }

    @Test
    void getAllProjectTasks_WhenProjectNotFound_ShouldReturn404() throws Exception {
        when(taskService.findAllProjectTasks(1L))
                .thenThrow(new ResourceNotFoundException("Project not found"));

        mockMvc.perform(get("/projects/1/tasks"))
                .andExpect(status().isNotFound());

        verify(taskService).findAllProjectTasks(1L);
    }

    @Test
    void getTask_WhenValidRequest_ShouldReturn200() throws Exception {
        when(taskService.findTaskById(10L, 1L)).thenReturn(responseDto);

        mockMvc.perform(get("/projects/1/tasks/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.assigneeId").value(2))
                .andExpect(jsonPath("$.taskStatus").value("TODO"));

        verify(taskService).findTaskById(10L, 1L);
    }

    @Test
    void getTask_WhenTaskNotFoundOrWrongProject_ShouldReturn404() throws Exception {
        when(taskService.findTaskById(10L, 1L))
                .thenThrow(new ResourceNotFoundException("Task not found in project"));

        mockMvc.perform(get("/projects/1/tasks/10"))
                .andExpect(status().isNotFound());

        verify(taskService).findTaskById(10L, 1L);
    }

    @Test
    void getTask_WhenInvalidTaskId_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/projects/1/tasks/not-a-number"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(taskService);
    }

    @Test
    void createTask_WhenValidRequest_ShouldReturn201() throws Exception {
        TaskRequestDto requestDto = new TaskRequestDto(2L, "Test Task", "Test description", LocalDate.of(2030, 1, 15));
        when(taskService.createTask(requestDto, 1L)).thenReturn(responseDto);

        mockMvc.perform(post("/projects/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "assigneeId": 2,
          "title": "Test Task",
          "description": "Test description",
          "deadline": "2030-01-15"
        }
        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.creatorId").value(1))
                .andExpect(jsonPath("$.assigneeId").value(2));

        verify(taskService).createTask(requestDto, 1L);
    }

    @Test
    void createTask_WhenMissingAssigneeId_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/projects/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "title": "Test Task",
          "description": "Test description",
          "deadline": "2030-01-15"
        }
        """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(taskService);
    }

    @Test
    void createTask_WhenMissingTitle_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/projects/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "assigneeId": 2,
          "description": "Test description",
          "deadline": "2030-01-15"
        }
        """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(taskService);
    }

    @Test
    void createTask_WhenMissingDescription_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/projects/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "assigneeId": 2,
          "title": "Test Task",
          "deadline": "2030-01-15"
        }
        """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(taskService);
    }

    @Test
    void createTask_WhenProjectOrAssigneeNotFound_ShouldReturn404() throws Exception {
        when(taskService.createTask(any(TaskRequestDto.class), eq(1L)))
                .thenThrow(new ResourceNotFoundException("Task creation rejected"));

        mockMvc.perform(post("/projects/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "assigneeId": 2,
          "title": "Test Task",
          "description": "Test description",
          "deadline": "2030-01-15"
        }
        """))
                .andExpect(status().isNotFound());

        verify(taskService).createTask(new TaskRequestDto(2L, "Test Task", "Test description", LocalDate.of(2030, 1, 15)), 1L);
    }

    @Test
    void createTask_WhenUnauthorized_ShouldReturn403() throws Exception {
        when(taskService.createTask(any(TaskRequestDto.class), eq(1L)))
                .thenThrow(new ForbiddenRequestException("Task creation rejected"));

        mockMvc.perform(post("/projects/1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "assigneeId": 2,
          "title": "Test Task",
          "description": "Test description",
          "deadline": "2030-01-15"
        }
        """))
                .andExpect(status().isForbidden());

        verify(taskService).createTask(new TaskRequestDto(2L, "Test Task", "Test description", LocalDate.of(2030, 1, 15)), 1L);
    }

    @Test
    void updateTask_WhenValidRequest_ShouldReturn200() throws Exception {
        TaskUpdateDto updateDto = new TaskUpdateDto("Updated title", "Updated description", LocalDate.of(2030, 2, 1));
        TaskResponseDto updatedResponse = new TaskResponseDto(
                1L, "Creator", 2L, "FirstName", "LastName", 1L, "Test Project",
                updateDto.title(), updateDto.description(), TaskStatus.TODO, updateDto.deadline());
        when(taskService.updateTask(10L, 1L, updateDto)).thenReturn(updatedResponse);

        mockMvc.perform(put("/projects/1/tasks/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "title": "Updated title",
          "description": "Updated description",
          "deadline": "2030-02-01"
        }
        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated title"))
                .andExpect(jsonPath("$.deadline").value("2030-02-01"));

        verify(taskService).updateTask(10L, 1L, updateDto);
    }

    @Test
    void updateTask_WhenTaskNotFoundOrWrongProject_ShouldReturn404() throws Exception {
        when(taskService.updateTask(eq(10L), eq(1L), any(TaskUpdateDto.class)))
                .thenThrow(new ResourceNotFoundException("Task update rejected"));

        mockMvc.perform(put("/projects/1/tasks/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "title": "Updated title",
          "description": "Updated description",
          "deadline": "2030-02-01"
        }
        """))
                .andExpect(status().isNotFound());

        verify(taskService).updateTask(10L, 1L, new TaskUpdateDto("Updated title", "Updated description", LocalDate.of(2030, 2, 1)));
    }

    @Test
    void updateTask_WhenUnauthorized_ShouldReturn403() throws Exception {
        when(taskService.updateTask(eq(10L), eq(1L), any(TaskUpdateDto.class)))
                .thenThrow(new ForbiddenRequestException("Task update rejected"));

        mockMvc.perform(put("/projects/1/tasks/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "title": "Updated title",
          "description": "Updated description",
          "deadline": "2030-02-01"
        }
        """))
                .andExpect(status().isForbidden());

        verify(taskService).updateTask(10L, 1L, new TaskUpdateDto("Updated title", "Updated description", LocalDate.of(2030, 2, 1)));
    }

    @Test
    void updateTask_WhenInvalidDeadline_ShouldReturn400() throws Exception {
        mockMvc.perform(put("/projects/1/tasks/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "title": "Updated title",
          "description": "Updated description",
          "deadline": "not-a-date"
        }
        """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(taskService);
    }

    @Test
    void changeTaskStatus_WhenValidRequest_ShouldReturn204() throws Exception {
        mockMvc.perform(patch("/projects/1/tasks/10/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "status": "DONE"
        }
        """))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(taskService).changeTaskStatus(10L, 1L, new ChangeTaskStatusDto(TaskStatus.DONE));
    }

    @Test
    void changeTaskStatus_WhenMissingStatus_ShouldReturn400() throws Exception {
        mockMvc.perform(patch("/projects/1/tasks/10/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {}
        """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(taskService);
    }

    @Test
    void changeTaskStatus_WhenInvalidStatus_ShouldReturn400() throws Exception {
        mockMvc.perform(patch("/projects/1/tasks/10/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "status": "UNKNOWN"
        }
        """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(taskService);
    }

    @Test
    void changeTaskStatus_WhenTaskNotFoundOrWrongProject_ShouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Status change rejected"))
                .when(taskService).changeTaskStatus(eq(10L), eq(1L), any(ChangeTaskStatusDto.class));

        mockMvc.perform(patch("/projects/1/tasks/10/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "status": "DONE"
        }
        """))
                .andExpect(status().isNotFound());

        verify(taskService).changeTaskStatus(10L, 1L, new ChangeTaskStatusDto(TaskStatus.DONE));
    }

    @Test
    void changeTaskStatus_WhenNoStateChange_ShouldReturn400() throws Exception {
        doThrow(new NoStateChangeException("Status change rejected"))
                .when(taskService).changeTaskStatus(eq(10L), eq(1L), any(ChangeTaskStatusDto.class));

        mockMvc.perform(patch("/projects/1/tasks/10/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "status": "DONE"
        }
        """))
                .andExpect(status().isBadRequest());

        verify(taskService).changeTaskStatus(10L, 1L, new ChangeTaskStatusDto(TaskStatus.DONE));
    }

    @Test
    void changeTaskStatus_WhenUnauthorized_ShouldReturn403() throws Exception {
        doThrow(new ForbiddenRequestException("Status change rejected"))
                .when(taskService).changeTaskStatus(eq(10L), eq(1L), any(ChangeTaskStatusDto.class));

        mockMvc.perform(patch("/projects/1/tasks/10/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "status": "DONE"
        }
        """))
                .andExpect(status().isForbidden());

        verify(taskService).changeTaskStatus(10L, 1L, new ChangeTaskStatusDto(TaskStatus.DONE));
    }

    @Test
    void changeTaskAssignee_WhenValidRequest_ShouldReturn204() throws Exception {
        mockMvc.perform(patch("/projects/1/tasks/10/assignee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "assigneeId": 2
        }
        """))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(taskService).changeTaskAssignee(new ChangeTaskAssigneeDto(2L), 1L, 10L);
    }

    @Test
    void changeTaskAssignee_WhenMissingAssignee_ShouldReturn400() throws Exception {
        mockMvc.perform(patch("/projects/1/tasks/10/assignee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {}
        """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(taskService);
    }

    @Test
    void changeTaskAssignee_WhenResourceNotFound_ShouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Assignee change rejected"))
                .when(taskService).changeTaskAssignee(any(ChangeTaskAssigneeDto.class), eq(1L), eq(10L));

        mockMvc.perform(patch("/projects/1/tasks/10/assignee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "assigneeId": 2
        }
        """))
                .andExpect(status().isNotFound());

        verify(taskService).changeTaskAssignee(new ChangeTaskAssigneeDto(2L), 1L, 10L);
    }

    @Test
    void changeTaskAssignee_WhenNoStateChange_ShouldReturn400() throws Exception {
        doThrow(new NoStateChangeException("Assignee change rejected"))
                .when(taskService).changeTaskAssignee(any(ChangeTaskAssigneeDto.class), eq(1L), eq(10L));

        mockMvc.perform(patch("/projects/1/tasks/10/assignee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "assigneeId": 2
        }
        """))
                .andExpect(status().isBadRequest());

        verify(taskService).changeTaskAssignee(new ChangeTaskAssigneeDto(2L), 1L, 10L);
    }

    @Test
    void changeTaskAssignee_WhenUnauthorized_ShouldReturn403() throws Exception {
        doThrow(new ForbiddenRequestException("Assignee change rejected"))
                .when(taskService).changeTaskAssignee(any(ChangeTaskAssigneeDto.class), eq(1L), eq(10L));

        mockMvc.perform(patch("/projects/1/tasks/10/assignee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "assigneeId": 2
        }
        """))
                .andExpect(status().isForbidden());

        verify(taskService).changeTaskAssignee(new ChangeTaskAssigneeDto(2L), 1L, 10L);
    }

    @Test
    void deleteTask_WhenValidRequest_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/projects/1/tasks/10"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(taskService).deleteTask(10L, 1L);
    }

    @Test
    void deleteTask_WhenTaskNotFoundOrWrongProject_ShouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Task deletion rejected"))
                .when(taskService).deleteTask(10L, 1L);

        mockMvc.perform(delete("/projects/1/tasks/10"))
                .andExpect(status().isNotFound());

        verify(taskService).deleteTask(10L, 1L);
    }

    @Test
    void deleteTask_WhenUnauthorized_ShouldReturn403() throws Exception {
        doThrow(new ForbiddenRequestException("Task deletion rejected"))
                .when(taskService).deleteTask(10L, 1L);

        mockMvc.perform(delete("/projects/1/tasks/10"))
                .andExpect(status().isForbidden());

        verify(taskService).deleteTask(10L, 1L);
    }
}
