package se.iths.armin.projectmanagerapi.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.iths.armin.projectmanagerapi.dto.ProjectMemberResponseDto;
import se.iths.armin.projectmanagerapi.dto.ProjectUserRequestDto;
import se.iths.armin.projectmanagerapi.dto.ProjectUserResponseDto;
import se.iths.armin.projectmanagerapi.dto.UserProjectResponseDto;
import se.iths.armin.projectmanagerapi.entity.AppUser;
import se.iths.armin.projectmanagerapi.entity.Project;
import se.iths.armin.projectmanagerapi.entity.ProjectUser;
import se.iths.armin.projectmanagerapi.service.AppUserService;
import se.iths.armin.projectmanagerapi.service.ProjectService;

@Component
@RequiredArgsConstructor
public class ProjectUserMapperImpl
        implements EntityMapper<ProjectUser, ProjectUserRequestDto, ProjectUserResponseDto> {

    private final ProjectService projectService;
    private final AppUserService appUserService;


    @Override
    public ProjectUser toEntity(ProjectUserRequestDto projectUserRequestDto) {
        if (projectUserRequestDto == null) {
            return null;
        }
        Project project = projectService.getProject(projectUserRequestDto.projectId());
        AppUser appUser = appUserService.getAppUser(projectUserRequestDto.appUserId());

        ProjectUser projectUser = new ProjectUser();
        projectUser.setProject(project);
        projectUser.setAppUser(appUser);

        return projectUser;
    }

    @Override
    public ProjectUserResponseDto toDto(ProjectUser projectUser) {
        if (projectUser == null) {
            return null;
        }

        Project project = projectUser.getProject();
        AppUser appUser = projectUser.getAppUser();

        return new ProjectUserResponseDto(
                projectUser.getProjectUserId(), project.getProjectId(), project.getTitle()
                , appUser.getUserid(), appUser.getFirstname(), appUser.getLastname(), projectUser.getProjectRole(), projectUser.getJoinedAt());

    }

    public ProjectMemberResponseDto toProjectMemberResponseDto(ProjectUser projectUser) {
        if (projectUser == null) {
            return null;
        }
        AppUser appUser = projectUser.getAppUser();


        return new ProjectMemberResponseDto(
                appUser.getUserid(), appUser.getFirstname(), appUser.getLastname(), projectUser.getProjectRole(), projectUser.getJoinedAt());
    }

    public UserProjectResponseDto toUserProjectResponseDto(ProjectUser projectUser) {
        if (projectUser == null) {
            return null;
        }
        Project project = projectUser.getProject();

        return new UserProjectResponseDto(
                project.getProjectId(), project.getTitle(), project.getProjectStatus(), projectUser.getProjectRole(), projectUser.getJoinedAt());
    }
}
