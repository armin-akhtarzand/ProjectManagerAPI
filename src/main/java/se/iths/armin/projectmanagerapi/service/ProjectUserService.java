package se.iths.armin.projectmanagerapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.iths.armin.projectmanagerapi.dto.ProjectUserRequestDto;
import se.iths.armin.projectmanagerapi.dto.ProjectUserResponseDto;
import se.iths.armin.projectmanagerapi.entity.AppUser;
import se.iths.armin.projectmanagerapi.entity.Project;
import se.iths.armin.projectmanagerapi.entity.ProjectUser;
import se.iths.armin.projectmanagerapi.entity.enums.ProjectRole;
import se.iths.armin.projectmanagerapi.exception.DuplicateFoundException;
import se.iths.armin.projectmanagerapi.exception.NoStateChangeException;
import se.iths.armin.projectmanagerapi.exception.ResourceNotFoundException;
import se.iths.armin.projectmanagerapi.mapper.ProjectUserMapperImpl;
import se.iths.armin.projectmanagerapi.repository.ProjectUserRepository;

@Service
@RequiredArgsConstructor
public class ProjectUserService {

    private final ProjectUserRepository projectUserRepository;
    private final AuthorizationService authorizationService;
    private final ProjectUserMapperImpl projectUserMapperImpl;


    public ProjectUserResponseDto addUserToProject(ProjectUserRequestDto projectUserRequestDto) {

        authorizationService.validateManagerOrAdmin();

        ProjectUser projectUser = projectUserMapperImpl.toEntity(projectUserRequestDto);


        boolean isMember = projectUserRepository.existsByAppUserAndProject(projectUser.getAppUser(), projectUser.getProject());

        if (isMember) {
            throw new DuplicateFoundException("User is already member of this project");
        }

        ProjectUser saved = projectUserRepository.save(projectUser);

        return projectUserMapperImpl.toDto(saved);
    }

    public void removeUserFromProject(ProjectUserRequestDto projectUserRequestDto) {

        authorizationService.validateManagerOrAdmin();
        ProjectUser projectUser = projectUserMapperImpl.toEntity(projectUserRequestDto);
        ProjectUser existingProjectUser = getProjectUser(projectUser.getAppUser(), projectUser.getProject());


        projectUserRepository.delete(existingProjectUser);
    }

    public ProjectUserResponseDto getUserFromProject(ProjectUserRequestDto projectUserRequestDto) {
        authorizationService.validateManagerOrAdmin();
        ProjectUser projectUser = projectUserMapperImpl.toEntity(projectUserRequestDto);
        ProjectUser existingProjectUser = getProjectUser(projectUser.getAppUser(), projectUser.getProject());

        return projectUserMapperImpl.toDto(existingProjectUser);
    }

    public void changeProjectRole(ProjectUserRequestDto projectUserRequestDto, ProjectRole newRole) {
        authorizationService.validateManagerOrAdmin();
        ProjectUser projectUser = projectUserMapperImpl.toEntity(projectUserRequestDto);
        ProjectUser existingProjectUser = getProjectUser(projectUser.getAppUser(), projectUser.getProject());


        if (existingProjectUser.getProjectRole().equals(newRole)) {
            throw new NoStateChangeException("Role is already: " + existingProjectUser.getProjectRole());
        }

        existingProjectUser.setProjectRole(newRole);
        projectUserRepository.save(existingProjectUser);
    }

    private ProjectUser getProjectUser(AppUser appUser, Project project) {
        ProjectUser existingProjectUser = projectUserRepository.findByAppUserAndProject(appUser, project)
                .orElseThrow(() -> new ResourceNotFoundException("ProjectUser not found"));
        return existingProjectUser;
    }

}
