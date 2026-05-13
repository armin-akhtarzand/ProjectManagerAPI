package se.iths.armin.projectmanagerapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.iths.armin.projectmanagerapi.dto.projectuser.ProjectMemberResponseDto;
import se.iths.armin.projectmanagerapi.dto.projectuser.ProjectUserRequestDto;
import se.iths.armin.projectmanagerapi.dto.projectuser.ProjectUserResponseDto;
import se.iths.armin.projectmanagerapi.dto.projectuser.UserProjectResponseDto;
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

@Service
@RequiredArgsConstructor
public class ProjectUserService {

    private final ProjectUserRepository projectUserRepository;
    private final AuthorizationService authorizationService;
    private final ProjectUserMapperImpl projectUserMapper;
    private final AppUserService appUserService;
    private final ProjectService projectService;

    @Transactional
    public ProjectUserResponseDto addUserToProject(ProjectUserRequestDto projectUserRequestDto) {
        authorizationService.validateProjectManagerOrAdmin(projectUserRequestDto.projectId());

        ProjectUser projectUser = projectUserMapper.toEntity(projectUserRequestDto);


        boolean isMember = projectUserRepository.existsByAppUserAndProject(projectUser.getAppUser(), projectUser.getProject());

        if (isMember) {
            throw new DuplicateFoundException("User is already member of this project");
        }

        ProjectUser saved = projectUserRepository.save(projectUser);

        return projectUserMapper.toDto(saved);
    }

    @Transactional
    public void removeUserFromProject(ProjectUserRequestDto projectUserRequestDto) {


        ProjectUser projectUser = projectUserMapper.toEntity(projectUserRequestDto);

        ProjectUser existingProjectUser = getProjectUser(projectUser.getAppUser(), projectUser.getProject());
        authorizationService.validateProjectManagerOrAdmin(existingProjectUser.getProject().getProjectId());


        projectUserRepository.delete(existingProjectUser);
    }

    public ProjectUserResponseDto getUserFromProject(ProjectUserRequestDto projectUserRequestDto) {
        authorizationService.validateProjectManagerOrAdmin(projectUserRequestDto.projectId());
        ProjectUser projectUser = projectUserMapper.toEntity(projectUserRequestDto);
        ProjectUser existingProjectUser = getProjectUser(projectUser.getAppUser(), projectUser.getProject());

        return projectUserMapper.toDto(existingProjectUser);
    }

    @Transactional
    public void changeProjectRole(ProjectUserRequestDto projectUserRequestDto, ProjectRole newRole) {
        authorizationService.validateProjectManagerOrAdmin(projectUserRequestDto.projectId());
        ProjectUser projectUser = projectUserMapper.toEntity(projectUserRequestDto);
        ProjectUser existingProjectUser = getProjectUser(projectUser.getAppUser(), projectUser.getProject());


        if (existingProjectUser.getProjectRole().equals(newRole)) {
            throw new NoStateChangeException("Role is already: " + existingProjectUser.getProjectRole());
        }

        existingProjectUser.setProjectRole(newRole);
        projectUserRepository.save(existingProjectUser);
    }

    public ProjectUser getProjectUser(AppUser appUser, Project project) {
        ProjectUser existingProjectUser = projectUserRepository.findByAppUserAndProject(appUser, project)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectUser not found"));
        return existingProjectUser;
    }

    public List<ProjectMemberResponseDto> getAllUsersFromProject(Long projectId) {

        Project project = projectService.getProject(projectId);

        List<ProjectUser> appUsersFromProject = projectUserRepository.findAllByProject(project);

        return appUsersFromProject.stream()
                .map(projectUserMapper::toProjectMemberResponseDto)
                .toList();
    }

    public List<UserProjectResponseDto> getAllProjectsFromUser(Long userId) {

        AppUser appUser = appUserService.getAppUser(userId);

        List<ProjectUser> projectsFromAppUser = projectUserRepository.findAllByAppUser(appUser);

        return projectsFromAppUser.stream()
                .map(projectUserMapper::toUserProjectResponseDto)
                .toList();
    }


}
