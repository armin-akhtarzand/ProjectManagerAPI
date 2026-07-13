package se.iths.armin.projectmanagerapi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import se.iths.armin.projectmanagerapi.dto.projectuser.ChangeProjectRoleDto;
import se.iths.armin.projectmanagerapi.dto.projectuser.ProjectUserRequestDto;
import se.iths.armin.projectmanagerapi.exception.DuplicateFoundException;
import se.iths.armin.projectmanagerapi.exception.NoStateChangeException;
import se.iths.armin.projectmanagerapi.exception.ResourceNotFoundException;
import se.iths.armin.projectmanagerapi.service.ProjectUserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectUserController.class)
@ActiveProfiles("test")
public class ProjectUserControllerTest {


    @MockitoBean
    private ProjectUserService projectUserService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllUsersFromProject_WhenValidRequest_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/projects/1/members"))
                .andExpect(status().isOk());

        verify(projectUserService).getAllUsersFromProject(1L);
    }

    @Test
    void getAllUsersFromProject_WhenNotFound_ShouldReturn404() throws Exception {
        doThrow(ResourceNotFoundException.class).when(projectUserService).getAllUsersFromProject(1L);
        mockMvc.perform(get("/projects/1/members"))
                .andExpect(status().isNotFound());

        verify(projectUserService).getAllUsersFromProject(1L);
    }

    @Test
    void getAllProjectsFromUser_WhenValidRequest_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/users/1/projects"))
                .andExpect(status().isOk());

        verify(projectUserService).getAllProjectsFromUser(1L);
    }

    @Test
    void getAllProjectsFromUser_WhenNotFound_ShouldReturn404() throws Exception {
        doThrow(ResourceNotFoundException.class).when(projectUserService).getAllProjectsFromUser(1L);
        mockMvc.perform(get("/users/1/projects"))
                .andExpect(status().isNotFound());

        verify(projectUserService).getAllProjectsFromUser(1L);
    }

    @Test
    void addUserToProject_WhenValidRequest_ShouldReturn201() throws Exception {
        mockMvc.perform(post("/projects/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "appUserId": 1,
                                "projectId": 1
                                }
                                """))
                .andExpect(status().isCreated());

        verify(projectUserService).addUserToProject(any(ProjectUserRequestDto.class));
    }

    @Test
    void addUserToProject_WhenMissingFields_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/projects/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "projectId": 1
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(projectUserService, never()).addUserToProject(any(ProjectUserRequestDto.class));
    }

    @Test
    void addUserToProject_WhenProjectOrUserNotFound_ShouldReturn404() throws Exception {
        doThrow(ResourceNotFoundException.class).
                when(projectUserService).addUserToProject(any(ProjectUserRequestDto.class));

        mockMvc.perform(post("/projects/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "appUserId": 1,
                                "projectId": 1
                                }
                                """))
                .andExpect(status().isNotFound());

        verify(projectUserService).addUserToProject(any(ProjectUserRequestDto.class));
    }

    @Test
    void addUserToProject_WhenDuplicateFound_ShouldReturn409() throws Exception {
        doThrow(DuplicateFoundException.class).
                when(projectUserService).addUserToProject(any(ProjectUserRequestDto.class));

        mockMvc.perform(post("/projects/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "appUserId": 1,
                                "projectId": 1
                                }
                                """))
                .andExpect(status().isConflict());

        verify(projectUserService).addUserToProject(any(ProjectUserRequestDto.class));
    }

    @Test
    void changeProjectRole_WhenValidRequest_ShouldReturn204() throws Exception {
        mockMvc.perform(patch("/projects/members/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "role": "PROJECT_MANAGER",
                                "appUserId": 1,
                                "projectId": 1
                                }
                                """))
                .andExpect(status().isNoContent());

        verify(projectUserService).changeProjectRole(any(ChangeProjectRoleDto.class));
    }

    @Test
    void changeProjectRole_WhenMissingFields_ShouldReturn400() throws Exception {
        mockMvc.perform(patch("/projects/members/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "appUserId": 1,
                                "projectId": 1
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(projectUserService, never()).changeProjectRole(any(ChangeProjectRoleDto.class));
    }

    @Test
    void changeProjectRole_WhenResourceNotFound_ShouldReturn404() throws Exception {
        doThrow(ResourceNotFoundException.class).
                when(projectUserService).changeProjectRole(any(ChangeProjectRoleDto.class));

        mockMvc.perform(patch("/projects/members/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "role": "PROJECT_MANAGER",
                                "appUserId": 2,
                                "projectId": 1
                                }
                                """))
                .andExpect(status().isNotFound());

        verify(projectUserService).changeProjectRole(any(ChangeProjectRoleDto.class));
    }

    @Test
    void changeProjectRole_WhenNoStateChange_ShouldReturn400() throws Exception {
        doThrow(NoStateChangeException.class).
                when(projectUserService).changeProjectRole(any(ChangeProjectRoleDto.class));

        mockMvc.perform(patch("/projects/members/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "role": "PROJECT_MEMBER",
                                "appUserId": 1,
                                "projectId": 1
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(projectUserService).changeProjectRole(any(ChangeProjectRoleDto.class));
    }

    @Test
    void removeUserFromProject_WhenValidRequest_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/projects/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "appUserId": 1,
                                "projectId": 1
                                }
                                """))
                .andExpect(status().isNoContent());

        verify(projectUserService).removeUserFromProject(any(ProjectUserRequestDto.class));
    }

    @Test
    void removeUserFromProject_WhenMissingFields_ShouldReturn400() throws Exception {
        mockMvc.perform(delete("/projects/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "appUserId": 1
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(projectUserService, never()).removeUserFromProject(any(ProjectUserRequestDto.class));
    }

    @Test
    void removeUserFromProject_WhenResourceNotFound_ShouldReturn404() throws Exception {
        doThrow(ResourceNotFoundException.class).
                when(projectUserService).removeUserFromProject(any(ProjectUserRequestDto.class));

        mockMvc.perform(delete("/projects/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "appUserId": 1,
                                "projectId": 1
                                }
                                """))
                .andExpect(status().isNotFound());

        verify(projectUserService).removeUserFromProject(any(ProjectUserRequestDto.class));
    }


}
