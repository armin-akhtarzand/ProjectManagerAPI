package se.iths.armin.projectmanagerapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import se.iths.armin.projectmanagerapi.dto.projectuser.*;
import se.iths.armin.projectmanagerapi.entity.AppUser;
import se.iths.armin.projectmanagerapi.entity.Project;
import se.iths.armin.projectmanagerapi.entity.ProjectUser;
import se.iths.armin.projectmanagerapi.entity.enums.ProjectRole;
import se.iths.armin.projectmanagerapi.mapper.ProjectUserMapperImpl;
import se.iths.armin.projectmanagerapi.repository.ProjectUserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProjectUserServiceTest {

    @Mock
    private ProjectUserRepository projectUserRepository;
    @Mock
    private ProjectUserMapperImpl projectUserMapper;
    @Mock
    private ProjectService projectService;
    @Mock
    private AppUserService appUserService;
    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private ProjectUserService projectUserService;

    private Project project;
    private AppUser user;
    private ProjectUser projectUser;
    private ProjectUserResponseDto responseDto;
    private ProjectUserRequestDto requestDto;


    @BeforeEach
    public void setUp() {
        project = new Project();
        project.setProjectId(1L);
        project.setTitle("Test Project");
        project.setDescription("A test project");

        user = new AppUser();
        user.setUserid(1L);
        user.setFirstname("Testname");
        user.setLastname("TestLastname");
        user.setEmail("test@test.com");
        user.setPassword("password");

        requestDto = new ProjectUserRequestDto(user.getUserid(), project.getProjectId());

        projectUser = new ProjectUser();
        projectUser.setProject(project);
        projectUser.setAppUser(user);

        responseDto = new ProjectUserResponseDto(
                projectUser.getProjectUserId(), project.getProjectId(), project.getTitle(),
                user.getUserid(), user.getFirstname(), user.getLastname(),
                projectUser.getProjectRole(), projectUser.getJoinedAt());
    }

    @Test
    void addUserToProject_WhenValidRequest_ShouldReturnProjectUserResponseDto() {
        Mockito.when(projectUserMapper.toEntity(requestDto)).thenReturn(projectUser);
        Mockito.when(projectUserRepository.save(any(ProjectUser.class))).thenReturn(projectUser);
        Mockito.when(projectUserMapper.toDto(projectUser)).thenReturn(responseDto);

        ProjectUserResponseDto result = projectUserService.addUserToProject(requestDto);

        assertEquals(result, responseDto);
        verify(authorizationService).validateProjectManagerOrAdmin(
                projectUser.getProject().getProjectId());
        assertNotNull(result);
        verify(projectUserRepository).save(projectUser);
    }

    @Test
    void removeUserFromProject_WhenValidRequest_ShouldDeleteProjectUser() {
        Mockito.when(projectUserMapper.toEntity(requestDto)).thenReturn(projectUser);
        Mockito.when(projectUserRepository.findByAppUserAndProject(user, project))
                .thenReturn(Optional.of(projectUser));


        projectUserService.removeUserFromProject(requestDto);

        verify(projectUserRepository).findByAppUserAndProject(user, project);
        verify(projectUserRepository).delete(projectUser);
        verify(authorizationService).validateProjectManagerOrAdmin(projectUser.getProject().getProjectId());
    }

    @Test
    void getAllUsersFromProject_WhenValidRequest_ShouldReturnProjectMemberResponseDto() {
        Mockito.when(projectService.getProject(any())).thenReturn(project);
        Mockito.when(projectUserRepository.findAllByProject(project)).thenReturn(List.of(projectUser));
        ProjectMemberResponseDto memberResponseDto = new ProjectMemberResponseDto(
                user.getUserid(), user.getFirstname(),
                user.getLastname(), projectUser.getProjectRole(), projectUser.getJoinedAt());
        Mockito.when(projectUserMapper.toProjectMemberResponseDto(
                projectUser)).thenReturn(memberResponseDto);

        List<ProjectMemberResponseDto> responseList =
                projectUserService.getAllUsersFromProject(project.getProjectId());

        assertNotNull(responseList);
        assertEquals(1, responseList.size());
        verify(projectUserRepository).findAllByProject(project);
        verify(projectUserMapper).toProjectMemberResponseDto(projectUser);
    }

    @Test
    void getAllProjectsFromUser_WhenValidRequest_ShouldReturnUserProjectResponseDto() {
        Mockito.when(appUserService.getAppUser(any())).thenReturn(user);
        Mockito.when(projectUserRepository.findAllByAppUser(user)).thenReturn(List.of(projectUser));
        UserProjectResponseDto userProjectResponseDto = new UserProjectResponseDto(
                project.getProjectId(), project.getTitle(), project.getProjectStatus(),
                projectUser.getProjectRole(), projectUser.getJoinedAt());
        Mockito.when(projectUserMapper.toUserProjectResponseDto(projectUser))
                .thenReturn(userProjectResponseDto);

        List<UserProjectResponseDto> responseList =
                projectUserService.getAllProjectsFromUser(user.getUserid());

        assertNotNull(responseList);
        assertEquals(1, responseList.size());
        verify(projectUserRepository).findAllByAppUser(user);
        verify(projectUserMapper).toUserProjectResponseDto(projectUser);
    }

    @Test
    void changeProjectRole_WhenValidRequest_ShouldChangeProjectRole() {
        Mockito.when(projectService.getProject(any())).thenReturn(project);
        Mockito.when(appUserService.getAppUser(any())).thenReturn(user);
        Mockito.when(projectUserRepository.findByAppUserAndProject(
                user, project)).thenReturn(Optional.of(projectUser));
        ChangeProjectRoleDto changeProjectRoleDto = new ChangeProjectRoleDto(
                ProjectRole.PROJECT_MANAGER, user.getUserid(), project.getProjectId());

        projectUserService.changeProjectRole(changeProjectRoleDto);

        verify(projectUserRepository).findByAppUserAndProject(user, project);
        verify(projectUserRepository).save(projectUser);
        assertEquals(ProjectRole.PROJECT_MANAGER, projectUser.getProjectRole());
        verify(authorizationService).validateProjectManagerOrAdmin(projectUser.getProject().getProjectId());
        verify(projectService).getProject(project.getProjectId());
        verify(appUserService).getAppUser(user.getUserid());
    }


}
