package se.iths.armin.projectmanagerapi.mapper;

import org.springframework.stereotype.Component;
import se.iths.armin.projectmanagerapi.dto.ProjectRequestDto;
import se.iths.armin.projectmanagerapi.dto.ProjectResponseDto;
import se.iths.armin.projectmanagerapi.entity.Project;

@Component
public class ProjectMapperImpl
        implements EntityMapper<Project, ProjectRequestDto, ProjectResponseDto> {
    @Override
    public Project toEntity(ProjectRequestDto projectRequestDto) {
        if (projectRequestDto == null) {
            return null;
        }
        Project project = new Project();
        project.setTitle(projectRequestDto.title());
        project.setDescription(projectRequestDto.description());


        return project;
    }

    @Override
    public ProjectResponseDto toDto(Project project) {
        if (project == null) {
            return null;
        }
        ProjectResponseDto responseDto = new ProjectResponseDto
                (project.getTitle(), project.getDescription(), project.getProjectStatus(), project.getCreatedAt());

        return responseDto;
    }

    @Override
    public void updateEntity(Project project, ProjectRequestDto projectRequestDto) {
        if (project == null) {
            return;
        }
        project.setTitle(projectRequestDto.title());
        project.setDescription(projectRequestDto.description());
    }
}
