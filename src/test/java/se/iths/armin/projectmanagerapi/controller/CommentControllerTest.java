package se.iths.armin.projectmanagerapi.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import se.iths.armin.projectmanagerapi.dto.comment.CommentRequestDto;
import se.iths.armin.projectmanagerapi.dto.comment.CommentResponseDto;
import se.iths.armin.projectmanagerapi.dto.comment.CommentUpdateDto;
import se.iths.armin.projectmanagerapi.exception.ForbiddenRequestException;
import se.iths.armin.projectmanagerapi.exception.ResourceNotFoundException;
import se.iths.armin.projectmanagerapi.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@ActiveProfiles("test")
public class CommentControllerTest {

    @MockitoBean
    private CommentService commentService;
    @Autowired
    private MockMvc mockMvc;

    private CommentResponseDto responseDto;

    @BeforeEach
    void setUp() {
        responseDto = new CommentResponseDto(
                "Test comment", 2L, "FirstName", "LastName",
                10L, LocalDateTime.of(2030, 1, 15, 12, 0));
    }

    @Test
    void getAllComments_WhenValidRequest_ShouldReturn200() throws Exception {
        when(commentService.findAllByTask(10L, 1L)).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/projects/1/tasks/10/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].content").value("Test comment"))
                .andExpect(jsonPath("$[0].fromId").value(2))
                .andExpect(jsonPath("$[0].taskId").value(10));

        verify(commentService).findAllByTask(10L, 1L);
    }

    @Test
    void getAllComments_WhenNoComments_ShouldReturnEmptyList() throws Exception {
        when(commentService.findAllByTask(10L, 1L)).thenReturn(List.of());

        mockMvc.perform(get("/projects/1/tasks/10/comments"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(commentService).findAllByTask(10L, 1L);
    }

    @Test
    void getAllComments_WhenTaskNotFoundOrWrongProject_ShouldReturn404() throws Exception {
        when(commentService.findAllByTask(10L, 1L))
                .thenThrow(new ResourceNotFoundException("Task not found in project"));

        mockMvc.perform(get("/projects/1/tasks/10/comments"))
                .andExpect(status().isNotFound());

        verify(commentService).findAllByTask(10L, 1L);
    }

    @Test
    void createComment_WhenValidRequest_ShouldReturn201() throws Exception {
        CommentRequestDto requestDto = new CommentRequestDto("Test comment");
        when(commentService.create(requestDto, 10L, 1L)).thenReturn(responseDto);

        mockMvc.perform(post("/projects/1/tasks/10/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "content": "Test comment"
        }
        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Test comment"))
                .andExpect(jsonPath("$.fromFirstname").value("FirstName"))
                .andExpect(jsonPath("$.taskId").value(10));

        verify(commentService).create(requestDto, 10L, 1L);
    }

    @Test
    void createComment_WhenResourceNotFound_ShouldReturn404() throws Exception {
        when(commentService.create(new CommentRequestDto("Test comment"), 10L, 1L))
                .thenThrow(new ResourceNotFoundException("Project, task or membership not found"));

        mockMvc.perform(post("/projects/1/tasks/10/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "content": "Test comment"
        }
        """))
                .andExpect(status().isNotFound());

        verify(commentService).create(new CommentRequestDto("Test comment"), 10L, 1L);
    }

    @Test
    void createComment_WhenMissingContent_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/projects/1/tasks/10/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {}
        """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(commentService);
    }

    @Test
    void createComment_WhenNullContent_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/projects/1/tasks/10/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "content": null
        }
        """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(commentService);
    }

    @Test
    void createComment_WhenBlankContent_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/projects/1/tasks/10/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "content": "   "
        }
        """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(commentService);
    }

    @Test
    void updateComment_WhenMissingContent_ShouldReturn400() throws Exception {
        mockMvc.perform(put("/projects/1/tasks/10/comments/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {}
        """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(commentService);
    }

    @Test
    void updateComment_WhenNullContent_ShouldReturn400() throws Exception {
        mockMvc.perform(put("/projects/1/tasks/10/comments/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "content": null
        }
        """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(commentService);
    }

    @Test
    void updateComment_WhenBlankContent_ShouldReturn400() throws Exception {
        mockMvc.perform(put("/projects/1/tasks/10/comments/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "content": "   "
        }
        """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(commentService);
    }

    @Test
    void createComment_WhenInvalidJson_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/projects/1/tasks/10/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(commentService);
    }

    @Test
    void updateComment_WhenValidRequest_ShouldReturn204() throws Exception {
        mockMvc.perform(put("/projects/1/tasks/10/comments/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "content": "Updated comment"
        }
        """))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(commentService).update(new CommentUpdateDto("Updated comment"), 100L, 10L, 1L);
    }

    @Test
    void updateComment_WhenCommentNotFoundOrWrongTaskOrProject_ShouldReturn404() throws Exception {
        CommentUpdateDto updateDto = new CommentUpdateDto("Updated comment");
        doThrow(new ResourceNotFoundException("Comment update rejected"))
                .when(commentService).update(updateDto, 100L, 10L, 1L);

        mockMvc.perform(put("/projects/1/tasks/10/comments/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "content": "Updated comment"
        }
        """))
                .andExpect(status().isNotFound());

        verify(commentService).update(updateDto, 100L, 10L, 1L);
    }

    @Test
    void deleteComment_WhenCommentNotFoundOrWrongTaskOrProject_ShouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Comment deletion rejected"))
                .when(commentService).delete(100L, 10L, 1L);

        mockMvc.perform(delete("/projects/1/tasks/10/comments/100"))
                .andExpect(status().isNotFound());

        verify(commentService).delete(100L, 10L, 1L);
    }

    @Test
    void updateComment_WhenUnauthorized_ShouldReturn403() throws Exception {
        CommentUpdateDto updateDto = new CommentUpdateDto("Updated comment");
        doThrow(new ForbiddenRequestException("Comment update rejected"))
                .when(commentService).update(updateDto, 100L, 10L, 1L);

        mockMvc.perform(put("/projects/1/tasks/10/comments/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
        {
          "content": "Updated comment"
        }
        """))
                .andExpect(status().isForbidden());

        verify(commentService).update(updateDto, 100L, 10L, 1L);
    }

    @Test
    void deleteComment_WhenUnauthorized_ShouldReturn403() throws Exception {
        doThrow(new ForbiddenRequestException("Comment deletion rejected"))
                .when(commentService).delete(100L, 10L, 1L);

        mockMvc.perform(delete("/projects/1/tasks/10/comments/100"))
                .andExpect(status().isForbidden());

        verify(commentService).delete(100L, 10L, 1L);
    }

    @Test
    void deleteComment_WhenValidRequest_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/projects/1/tasks/10/comments/100"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(commentService).delete(100L, 10L, 1L);
    }

    @Test
    void deleteComment_WhenInvalidProjectId_ShouldReturn400() throws Exception {
        mockMvc.perform(delete("/projects/invalid/tasks/10/comments/100"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(commentService);
    }

    @Test
    void deleteComment_WhenInvalidTaskId_ShouldReturn400() throws Exception {
        mockMvc.perform(delete("/projects/1/tasks/invalid/comments/100"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(commentService);
    }

    @Test
    void deleteComment_WhenInvalidCommentId_ShouldReturn400() throws Exception {
        mockMvc.perform(delete("/projects/1/tasks/10/comments/invalid"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(commentService);
    }
}
