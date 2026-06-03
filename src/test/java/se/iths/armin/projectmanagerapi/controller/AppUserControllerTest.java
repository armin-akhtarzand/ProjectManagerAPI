package se.iths.armin.projectmanagerapi.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import se.iths.armin.projectmanagerapi.exception.DuplicateFoundException;
import se.iths.armin.projectmanagerapi.exception.NoStateChangeException;
import se.iths.armin.projectmanagerapi.exception.ResourceNotFoundException;
import se.iths.armin.projectmanagerapi.service.AppUserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AppUserController.class)
@ActiveProfiles("test")
public class AppUserControllerTest {


    @MockitoBean
    private AppUserService appUserService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllAppUsersShouldReturn200() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(appUserService).findAllAppUsers();
    }

    @Test
    void findByIdShouldReturn200() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());

        verify(appUserService).findById(eq(1L));
    }

    @Test
    void findByIdNotFoundShouldReturn404() throws Exception {
        doThrow(ResourceNotFoundException.class).when(appUserService).findById(eq(1L));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound());

        verify(appUserService).findById(eq(1L));
    }

    @Test
    void createAppUserShouldReturn201() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("""
                                {
                                  "email": "test@test.se",
                                  "password": "Password123!",
                                  "firstname": "Testname",
                                  "lastname": "Testlastname"
                                }
                                """)))
                .andExpect(status().isCreated());


        verify(appUserService).createAppUser(any());

    }

    @Test
    void createAppUserWithMissingFieldsShouldReturn400() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("""
                                {
                                  "email": "test@test.se",
                                  "password": "Password123!",
                                  "firstname": "Testname"
                                }
                                """)))
                .andExpect(status().isBadRequest());

        verify(appUserService, never()).createAppUser(any());
    }

    @Test
    void createAppUserWithDuplicateFieldsShouldReturn409() throws Exception {
        doThrow(DuplicateFoundException.class).when(appUserService).createAppUser(any());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("""
                                {
                                  "email": "test@test.se",
                                  "password": "Password123!",
                                  "firstname": "Testname",
                                  "lastname": "Testlastname"
                                }
                                """)))
                .andExpect(status().isConflict());

        verify(appUserService).createAppUser(any());
    }

    @Test
    void deleteAppUserShouldReturn204() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(appUserService).deleteAppUser(eq(1L));
    }

    @Test
    void deleteAppUserNotFoundShouldReturn404() throws Exception {
        doThrow(ResourceNotFoundException.class)
                .when(appUserService).deleteAppUser(2L);

        mockMvc.perform(delete("/users/2"))
                .andExpect(status().isNotFound());

        verify(appUserService).deleteAppUser(eq(2L));
    }

    @Test
    void updateAppUserShouldReturn200() throws Exception {
        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("""
                                {
                                  "email": "test@test.se",
                                  "firstname": "Testname",
                                  "lastname": "Testlastname"
                                }
                                """)))
                .andExpect(status().isOk());

        verify(appUserService).updateAppUser(eq(1L), any());
    }

    @Test
    void updateAppUserIdNotFoundShouldReturn404() throws Exception {
        doThrow(ResourceNotFoundException.class)
                .when(appUserService).updateAppUser(eq(2L), any());

        mockMvc.perform(put("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("""
                                {
                                  "email": "test@test.se",
                                  "firstname": "Testname",
                                  "lastname": "Testlastname"
                                }
                                """)))
                .andExpect(status().isNotFound());

        verify(appUserService).updateAppUser(eq(2L), any());
    }

    @Test
    void updateAppUserDuplicateEmailShouldReturn409() throws Exception {
        doThrow(DuplicateFoundException.class).when(appUserService).updateAppUser(eq(2L), any());

        mockMvc.perform(put("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("""
                                {
                                  "email": "test@test.se",
                                  "firstname": "Tester",
                                  "lastname": "TesterLast"
                                }
                                """)))
                .andExpect(status().isConflict());


        verify(appUserService).updateAppUser(eq(2L), any());
    }

    @Test
    void updateAppUserMissingFieldsShouldReturn400() throws Exception {
        mockMvc.perform(put("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("""
                                {
                                  "firstname": "Tester",
                                  "lastname": "TesterLast"
                                }
                                """)))
                .andExpect(status().isBadRequest());

        verify(appUserService, never()).updateAppUser(eq(2L), any());
    }

    @Test
    void patchAppUserPasswordShouldReturn204() throws Exception {
        mockMvc.perform(patch("/users/1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("""
                                {
                                  "oldPassword": "OldPassword123!",
                                  "newPassword": "NewPassword123!"
                                }
                                """)))
                .andExpect(status().isNoContent());

        verify(appUserService).changePassword(eq(1L), any());
    }

    @Test
    void patchAppUserPasswordWithoutOldPasswordShouldReturn400() throws Exception {
        mockMvc.perform(patch("/users/1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("""
                                {
                                  "newPassword": "NewPassword123!"
                                }
                                """)))
                .andExpect(status().isBadRequest());

        verify(appUserService, never()).changePassword(eq(1L), any());
    }

    @Test
    void patchAppUserPasswordWithoutNewPasswordShouldReturn400() throws Exception {
        mockMvc.perform(patch("/users/1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("""
                                {
                                  "oldPassword": "OldPassword123!"
                                }
                                """)))
                .andExpect(status().isBadRequest());

        verify(appUserService, never()).changePassword(eq(1L), any());
    }

    @Test
    void patchAppUserStatusShouldReturn204() throws Exception {
        mockMvc.perform(patch("/users/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("""
                                {
                                  "status": "INACTIVE"
                                }
                                """)))
                .andExpect(status().isNoContent());

        verify(appUserService).changeUserStatus(eq(1L), any());
    }

    @Test
    void patchAppUserStatusNoStateChangeShouldReturn400() throws Exception {
        doThrow(NoStateChangeException.class).when(appUserService).changeUserStatus(eq(1L), any());

        mockMvc.perform(patch("/users/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("""
                                {
                                  "status": "ACTIVE"
                                }
                                """)))
                .andExpect(status().isBadRequest());

        verify(appUserService).changeUserStatus(eq(1L), any());
    }

    @Test
    void patchAppUserPositionShouldReturn204() throws Exception {
        mockMvc.perform(patch("/users/1/position")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("""
                                {
                                  "position": "ADMIN"
                                }
                                """)))
                .andExpect(status().isNoContent());

        verify(appUserService).changeUserPosition(eq(1L), any());
    }

    @Test
    void patchAppUserPositionNoStateChangeShouldReturn400() throws Exception {
        doThrow(NoStateChangeException.class).when(appUserService).changeUserPosition(eq(1L), any());

        mockMvc.perform(patch("/users/1/position")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("""
                                {
                                  "position": "EMPLOYEE"
                                }
                                """)))
                .andExpect(status().isBadRequest());

        verify(appUserService).changeUserPosition(eq(1L), any());
    }


}
