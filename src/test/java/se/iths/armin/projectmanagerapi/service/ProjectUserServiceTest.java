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
import se.iths.armin.projectmanagerapi.exception.DuplicateFoundException;
import se.iths.armin.projectmanagerapi.exception.NoStateChangeException;
import se.iths.armin.projectmanagerapi.exception.ResourceNotFoundException;
import se.iths.armin.projectmanagerapi.mapper.ProjectUserMapperImpl;
import se.iths.armin.projectmanagerapi.repository.ProjectUserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        Mockito.when(projectUserRepository.save(projectUser)).thenReturn(projectUser);
        Mockito.when(projectUserMapper.toDto(projectUser)).thenReturn(responseDto);

        ProjectUserResponseDto result = projectUserService.addUserToProject(requestDto);

        assertEquals(responseDto, result);
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
        Mockito.when(projectService.getProject(project.getProjectId())).thenReturn(project);
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
        Mockito.when(appUserService.getAppUser(user.getUserid())).thenReturn(user);
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
        Mockito.when(projectService.getProject(project.getProjectId())).thenReturn(project);
        Mockito.when(appUserService.getAppUser(user.getUserid())).thenReturn(user);
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

    @Test
    void addUserToProject_WhenDuplicate_ShouldThrowException() {
        Mockito.when(projectUserMapper.toEntity(requestDto)).thenReturn(projectUser);
        Mockito.when(projectUserRepository.existsByAppUserAndProject(user, project)).thenReturn(Boolean.TRUE);


        assertThrows(DuplicateFoundException.class,
                () -> projectUserService.addUserToProject(requestDto));
        verify(projectUserMapper).toEntity(requestDto);
        verify(projectUserRepository, never()).save(projectUser);
    }

    @Test
    void removeUserFromProject_WhenNotFound_ShouldThrowException() {
        when(projectUserMapper.toEntity(requestDto)).thenReturn(projectUser);
        when(projectUserRepository.findByAppUserAndProject
                (user, project)).thenReturn(Optional.empty());


        assertThrows(ResourceNotFoundException.class,
                () -> projectUserService.removeUserFromProject(requestDto));
        verify(projectUserRepository).findByAppUserAndProject(user, project);
        verify(projectUserMapper).toEntity(requestDto);
        verify(projectUserRepository, never()).delete(projectUser);
    }

    @Test
    void getAllUsersFromProject_WhenNotFound_ShouldThrowException() {
        ;
        when(projectService.getProject(2L)).thenThrow(ResourceNotFoundException.class);


        assertThrows(ResourceNotFoundException.class,
                () -> projectUserService.getAllUsersFromProject(2L));
        verify(projectService).getProject(2L);
        verify(projectUserRepository, never()).findAllByProject(any());
    }

    @Test
    void getAllProjectsFromUser_WhenNotFound_ShouldThrowException() {
        when(appUserService.getAppUser(2L)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class,
                () -> projectUserService.getAllProjectsFromUser(2L));

        verify(appUserService).getAppUser(2L);
        verify(projectUserRepository, never()).findAllByAppUser(any());
    }

    @Test
    void changeProjectRole_WhenNoStateChange_ShouldThrowException() {
        when(projectUserRepository.findByAppUserAndProject
                (user, project)).thenReturn(Optional.of(projectUser));
        when(projectService.getProject(project.getProjectId())).thenReturn(project);
        when(appUserService.getAppUser(user.getUserid())).thenReturn(user);
        ChangeProjectRoleDto changeProjectRoleDto =
                new ChangeProjectRoleDto(ProjectRole.PROJECT_MEMBER, user.getUserid(), project.getProjectId());


        assertThrows(NoStateChangeException.class,
                () -> projectUserService.changeProjectRole(changeProjectRoleDto));
        verify(projectUserRepository).findByAppUserAndProject(user, project);
        verify(projectUserRepository, never()).save(projectUser);
    }

    @Test
    void changeProjectRole_WhenUserNotFound_ShouldThrowException() {
        when(projectService.getProject(project.getProjectId())).thenReturn(project);
        when(appUserService.getAppUser(2L)).thenReturn(null);
        ChangeProjectRoleDto changeProjectRoleDto = new ChangeProjectRoleDto
                (ProjectRole.PROJECT_MEMBER, 2L, project.getProjectId());


        assertThrows(ResourceNotFoundException.class,
                () -> projectUserService.changeProjectRole(changeProjectRoleDto));
        verify(projectUserRepository, never()).findByAppUserAndProject(user, project);
        verify(projectUserRepository, never()).save(projectUser);
    }

    @Test
    void changeProjectRole_WhenProjectNotFound_ShouldThrowException() {
        when(projectService.getProject(2L)).thenReturn(null);
        when(appUserService.getAppUser(user.getUserid())).thenReturn(user);
        ChangeProjectRoleDto changeProjectRoleDto = new ChangeProjectRoleDto
                (ProjectRole.PROJECT_MEMBER, user.getUserid(), 2L);


        assertThrows(ResourceNotFoundException.class,
                () -> projectUserService.changeProjectRole(changeProjectRoleDto));
        verify(projectUserRepository, never()).findByAppUserAndProject(user, project);
        verify(projectUserRepository, never()).save(projectUser);
    }

    @Test
    void changeProjectRole_UserNotMember_ShouldThrowException() {
        when(projectService.getProject(project.getProjectId())).thenReturn(project);
        when(appUserService.getAppUser(user.getUserid())).thenReturn(user);
        when(projectUserRepository.findByAppUserAndProject(user, project)).thenReturn(Optional.empty());
        ChangeProjectRoleDto changeProjectRoleDto = new ChangeProjectRoleDto
                (ProjectRole.PROJECT_MEMBER, user.getUserid(), project.getProjectId());

        assertThrows(ResourceNotFoundException.class,
                () -> projectUserService.changeProjectRole(changeProjectRoleDto));
        verify(projectUserRepository).findByAppUserAndProject(user, project);
        verify(projectService).getProject(project.getProjectId());
        verify(appUserService).getAppUser(user.getUserid());
        verify(projectUserRepository, never()).save(projectUser);
    }


}
