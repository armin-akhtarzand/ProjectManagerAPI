package se.iths.armin.projectmanagerapi.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import se.iths.armin.projectmanagerapi.controller.AppUserController;
import se.iths.armin.projectmanagerapi.service.AppUserService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppUserController.class)
@AutoConfigureMockMvc(addFilters = true)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class AppUserSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppUserService appUserService;

    @Test
    void getAllUsersWithoutJwtShouldReturn401() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllUsersByEmployeeShouldReturn403() throws Exception {
        mockMvc.perform(get("/users")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllUsersByAdminShouldReturn200() throws Exception {
        mockMvc.perform(get("/users")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    void findByIdWithoutJwtShouldReturn401() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findByIdWithJwtShouldReturn200() throws Exception {
        mockMvc.perform(get("/users/1").with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    void createAppUserWithoutJwtShouldReturn401() throws Exception {
        mockMvc.perform(post("/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createAppUserByEmployeeShouldReturn403() throws Exception {
        mockMvc.perform(post("/users")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void createAppUserByAdminShouldReturn201() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "test@test.se",
                                  "password": "Password123!",
                                  "firstname": "Testname",
                                  "lastname": "Testlastname"
                                }
                                """)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isCreated());
    }

    @Test
    void deleteAppUserWithoutJwtShouldReturn401() throws Exception {
        mockMvc.perform(delete(("/users/1")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteAppUserByEmployeeShouldReturn403() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteAppUserByAdminShouldReturn204() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateAppUserWithoutJwtShouldReturn401() throws Exception {
        mockMvc.perform(put("/users/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateAppUserWithJwtShouldReturn200() throws Exception {
        mockMvc.perform(put("/users/1").with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "new@test.se",
                                  "firstname": "Testname",
                                  "lastname": "Testlastname"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void patchAppUserPasswordWithoutJwtShouldReturn401() throws Exception {
        mockMvc.perform(patch("/users/1/password"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void patchAppUserPasswordWithJwtShouldReturn204() throws Exception {
        mockMvc.perform(patch("/users/1/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "oldPassword": "oldpassword123",
                                "newPassword": "newpassword123"
                                }
                                """)
                        .with(jwt()))
                .andExpect(status().isNoContent());
    }

    @Test
    void patchAppUserStatusWithoutJwtShouldReturn401() throws Exception {
        mockMvc.perform(patch("/users/1/status"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void patchAppUserStatusWithJwtShouldReturn204() throws Exception {
        mockMvc.perform(patch("/users/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "status": "VACATION"
                                }
                                """)
                        .with(jwt()))
                .andExpect(status().isNoContent());
    }

    @Test
    void patchAppUserPositionWithoutJwtShouldReturn401() throws Exception {
        mockMvc.perform(patch("/users/1/position"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void patchAppUserPositionByEmployeeShouldReturn403() throws Exception {
        mockMvc.perform(patch("/users/1/position")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void patchAppUserPositionByAdminShouldReturn204() throws Exception {
        mockMvc.perform(patch("/users/1/position")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("""
                                {
                                  "position": "ADMIN"
                                }
                                """))
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNoContent());
    }


}
