package se.iths.armin.projectmanagerapi;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.iths.armin.projectmanagerapi.dto.ChangeProjectStatusDto;
import se.iths.armin.projectmanagerapi.dto.ProjectRequestDto;
import se.iths.armin.projectmanagerapi.dto.ProjectResponseDto;
import se.iths.armin.projectmanagerapi.entity.Project;
import se.iths.armin.projectmanagerapi.exception.NoStateChangeException;
import se.iths.armin.projectmanagerapi.exception.ResourceNotFoundException;
import se.iths.armin.projectmanagerapi.mapper.ProjectMapperImpl;
import se.iths.armin.projectmanagerapi.repository.ProjectRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {


    private final ProjectRepository projectRepository;
    private final AuthorizationService authorizationService;
    private final ProjectMapperImpl projectMapper;


    public ProjectResponseDto createProject(ProjectRequestDto projectRequestDto) {

        authorizationService.validateAdmin();

        Project project = projectMapper.toEntity(projectRequestDto);
        project = projectRepository.save(project);

        return projectMapper.toDto(project);
    }

    public ProjectResponseDto updateProject(ProjectRequestDto projectRequestDto, Long projectId) {

        authorizationService.validateManagerOrAdmin();
        Project project = getProject(projectId);

        projectMapper.updateEntity(project, projectRequestDto);

        project = projectRepository.save(project);

        return projectMapper.toDto(project);
    }

    public ProjectResponseDto findProjectById(long projectId) {
        Project project = getProject(projectId);

        ProjectResponseDto responseDto = projectMapper.toDto(project);


        return responseDto;
    }

    public void deleteProjectById(Long projectId) {
        authorizationService.validateAdmin();

        Project project = getProject(projectId);

        projectRepository.delete(project);
    }


    public Project getProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    public List<ProjectResponseDto> getAllProjects() {
        List<Project> projects = projectRepository.findAll();

        return projects.stream().map(projectMapper::toDto).toList();
    }

    public void changeProjectStatus(Long projectId, ChangeProjectStatusDto changeProjectStatusDto) {
        
        authorizationService.validateManagerOrAdmin();
        Project project = getProject(projectId);

        if (project.getProjectStatus().equals(changeProjectStatusDto.status())) {
            throw new NoStateChangeException("Project already has this status");
        }
        project.setProjectStatus(changeProjectStatusDto.status());
        projectRepository.save(project);

    }


}
