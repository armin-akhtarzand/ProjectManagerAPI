package se.iths.armin.projectmanagerapi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import se.iths.armin.projectmanagerapi.exception.ResourceNotFoundException;
import se.iths.armin.projectmanagerapi.service.ProjectService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@ActiveProfiles("test")
public class ProjectControllerTest {

    @MockitoBean
    private ProjectService projectService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllProjects_WhenValidRequest_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk());

        verify(projectService).getAllProjects();
    }

    @Test
    void getProjectById_WhenValidRequest_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/projects/1"))
                .andExpect(status().isOk());

        verify(projectService).findProjectById(1L);
    }

    @Test
    void getProjectById_WhenInvalidRequest_ShouldReturn404() throws Exception {
        doThrow(ResourceNotFoundException.class).when(projectService).findProjectById(2L);

        mockMvc.perform(get("/projects/2"))
                .andExpect(status().isNotFound());

        verify(projectService).findProjectById(2L);
    }

    @Test
    void createProject_WhenValidRequest_ShouldReturn201() throws Exception {
        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("""
                                {
                                  "title":"Test Project",
                                  "description":"Test Project"
                                }
                                """)))
                .andExpect(status().isCreated());

        verify(projectService).createProject(any());
    }

    @Test
    void createProject_WhenMissingFields_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("""
                                {
                                  "description":"Test Project"
                                }
                                """)))
                .andExpect(status().isBadRequest());

        verify(projectService, never()).createProject(any());
    }

    @Test
    void deleteProject_WhenValidRequest_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/projects/1"))
                .andExpect(status().isNoContent());

        verify(projectService).deleteProjectById(1L);
    }

    @Test
    void deleteProject_WhenProjectNotFound_ShouldReturn404() throws Exception {
        doThrow(ResourceNotFoundException.class).when(projectService).deleteProjectById(2L);

        mockMvc.perform(delete("/projects/2"))
                .andExpect(status().isNotFound());


        verify(projectService).deleteProjectById(2L);
    }

    @Test
    void updateProject_WhenValidRequest_ShouldReturn200() throws Exception {
        mockMvc.perform(put("/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("""
                                {
                                "title":"update",
                                "description":"update"
                                }
                                """)))
                .andExpect(status().isOk());

        verify(projectService).updateProject(any(), eq(1L));
    }

    @Test
    void updateProject_WhenProjectNotFound_ShouldReturn404() throws Exception {
        doThrow(ResourceNotFoundException.class).when(projectService).updateProject(any(), eq(1L));
        mockMvc.perform(put("/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("""
                                {
                                "title":"update",
                                "description":"update"
                                }
                                """)))
                .andExpect(status().isNotFound());

        verify(projectService).updateProject(any(), eq(1L));
    }

    @Test
    void updateProject_WhenMissingFields_ShouldReturn400() throws Exception {
        mockMvc.perform(put("/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("""
                                {
                                "description":"update"
                                }
                                """)))
                .andExpect(status().isBadRequest());

        verify(projectService, never()).updateProject(any(), eq(1L));

    }

    @Test
    void changeProjectStatus_WhenValidRequest_ShouldReturn204() throws Exception {
        mockMvc.perform(patch("/projects/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "status": "COMPLETED"
                                }
                                """))
                .andExpect(status().isNoContent());

        verify(projectService).changeProjectStatus(eq(1L), any());
    }

    @Test
    void changeProjectStatus_WhenProjectNotFound_ShouldReturn404() throws Exception {
        doThrow(ResourceNotFoundException.class).when(projectService).changeProjectStatus(eq(1L), any());
        mockMvc.perform(patch("/projects/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "status": "COMPLETED"
                                }
                                """))
                .andExpect(status().isNotFound());

        verify(projectService).changeProjectStatus(eq(1L), any());
    }

    @Test
    void changeProjectStatus_WhenMissingFields_ShouldReturn400() throws Exception {
        mockMvc.perform(patch("/projects/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "status":""
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(projectService, never()).changeProjectStatus(eq(1L), any());
    }
}
