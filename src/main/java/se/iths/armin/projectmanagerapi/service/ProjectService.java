package se.iths.armin.projectmanagerapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.iths.armin.projectmanagerapi.dto.project.ChangeProjectStatusDto;
import se.iths.armin.projectmanagerapi.dto.project.ProjectRequestDto;
import se.iths.armin.projectmanagerapi.dto.project.ProjectResponseDto;
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


    @Transactional
    public ProjectResponseDto createProject(ProjectRequestDto projectRequestDto) {

        authorizationService.validateAdmin();

        Project project = projectMapper.toEntity(projectRequestDto);
        Project saved = projectRepository.save(project);

        return projectMapper.toDto(saved);
    }

    @Transactional
    public ProjectResponseDto updateProject(ProjectRequestDto projectRequestDto, Long projectId) {

        authorizationService.validateProjectManagerOrAdmin(projectId);
        Project project = getProject(projectId);

        projectMapper.updateEntity(project, projectRequestDto);

        Project updated = projectRepository.save(project);

        return projectMapper.toDto(updated);
    }

    public ProjectResponseDto findProjectById(long projectId) {
        Project project = getProject(projectId);

        ProjectResponseDto responseDto = projectMapper.toDto(project);


        return responseDto;
    }

    @Transactional
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

        authorizationService.validateProjectManagerOrAdmin(projectId);
        Project project = getProject(projectId);

        if (project.getProjectStatus().equals(changeProjectStatusDto.status())) {
            throw new NoStateChangeException("Project already has this status");
        }
        project.setProjectStatus(changeProjectStatusDto.status());
        projectRepository.save(project);

    }


}
