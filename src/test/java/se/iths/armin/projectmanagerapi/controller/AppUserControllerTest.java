package se.iths.armin.projectmanagerapi.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import se.iths.armin.projectmanagerapi.service.AppUserService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AppUserController.class)
public class AppUserControllerTest {


    @MockitoBean
    private AppUserService appUserService;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private JwtDecoder jwtDecoder;


    @Test
    void getAllAppUsersShouldReturn200() throws Exception {
        mockMvc.perform(get("/users")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    void getAppUserShouldReturn200() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAppUserShouldReturn200ForUser() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());
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
                                  "lastname": "Testlastname",
                                  "position": "EMPLOYEE",
                                  "status": "ACTIVE"
                                }
                                """)))
                .andExpect(status().isCreated());

    }

    @Test
    void deleteAppUserShouldReturn204() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateAppUserShouldReturn204() throws Exception {

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
                .andExpect(status().isOk());
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
                .andExpect(status().isOk());

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
                .andExpect(status().isOk());

    }


}
