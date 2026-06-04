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
    void getAllUsers_WithoutJwt_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllUsers_ByEmployee_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/users")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllUsers_ByAdmin_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/users")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    void findById_WithoutJwt_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findById_WithJwt_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/users/1").with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    void createAppUser_WithoutJwt_ShouldReturn401() throws Exception {
        mockMvc.perform(post("/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createAppUser_ByEmployee_ShouldReturn403() throws Exception {
        mockMvc.perform(post("/users")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void createAppUser_ByAdmin_ShouldReturn201() throws Exception {
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
    void deleteAppUser_WithoutJwt_ShouldReturn401() throws Exception {
        mockMvc.perform(delete(("/users/1")))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void deleteAppUser_ByEmployee_ShouldReturn403() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteAppUser_ByAdmin_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateAppUser_WithoutJwt_ShouldReturn401() throws Exception {
        mockMvc.perform(put("/users/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateAppUser_WithJwt_ShouldReturn200() throws Exception {
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
    void patchAppUserPassword_WithoutJwt_ShouldReturn401() throws Exception {
        mockMvc.perform(patch("/users/1/password"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void patchAppUserPassword_WithJwt_ShouldReturn204() throws Exception {
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
    void patchAppUserStatus_WithoutJwt_ShouldReturn401() throws Exception {
        mockMvc.perform(patch("/users/1/status"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void patchAppUserStatus_WithJwt_ShouldReturn204() throws Exception {
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
    void patchAppUserPosition_WithoutJwt_ShouldReturn401() throws Exception {
        mockMvc.perform(patch("/users/1/position"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void patchAppUserPosition_ByEmployee_ShouldReturn403() throws Exception {
        mockMvc.perform(patch("/users/1/position")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void patchAppUserPosition_ByAdmin_ShouldReturn204() throws Exception {
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
