package se.iths.armin.projectmanagerapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import se.iths.armin.projectmanagerapi.dto.project.ChangeProjectStatusDto;
import se.iths.armin.projectmanagerapi.dto.project.ProjectRequestDto;
import se.iths.armin.projectmanagerapi.dto.project.ProjectResponseDto;
import se.iths.armin.projectmanagerapi.entity.Project;
import se.iths.armin.projectmanagerapi.entity.enums.ProjectStatus;
import se.iths.armin.projectmanagerapi.exception.NoStateChangeException;
import se.iths.armin.projectmanagerapi.exception.ResourceNotFoundException;
import se.iths.armin.projectmanagerapi.mapper.ProjectMapperImpl;
import se.iths.armin.projectmanagerapi.repository.ProjectRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private AuthorizationService authorizationService;
    @Mock
    private ProjectMapperImpl projectMapper;
    @InjectMocks
    private ProjectService projectService;

    private Project project;
    private ProjectRequestDto requestDto;
    private ProjectResponseDto responseDto;


    @BeforeEach
    public void setup() {

        requestDto = new ProjectRequestDto("Test Project", "Description");

        project = new Project();
        project.setDescription(requestDto.description());
        project.setTitle(requestDto.title());
        project.setProjectId(1L);

        responseDto = new ProjectResponseDto(
                project.getTitle(),
                project.getDescription(),
                project.getProjectStatus(),
                project.getCreatedAt()
        );
    }

    @Test
    void createProject_WhenValidRequest_ShouldReturnProjectResponseDto() {

        Mockito.when(projectMapper.toEntity(requestDto)).thenReturn(project);
        Mockito.when(projectRepository.save(any(Project.class))).thenReturn(project);
        Mockito.when(projectMapper.toDto(project)).thenReturn(responseDto);

        ProjectResponseDto result = projectService.createProject(requestDto);


        assertEquals(result, responseDto);
        verify(authorizationService).validateAdmin();
        assertNotNull(result);
        assertEquals(requestDto.title(), result.title());
    }

    @Test
    void updateProject_WhenValidRequest_ShouldSaveUpdatedProject() {

        Mockito.when(projectRepository.findById(1L))

                .thenReturn(Optional.of(project));

        Mockito.doNothing()
                .when(projectMapper).updateEntity(project, requestDto);


        projectService.updateProject(requestDto, 1L);

        verify(authorizationService).validateProjectManagerOrAdmin(1L);
        verify(projectRepository).findById(1L);
        verify(projectMapper).updateEntity(project, requestDto);
        verify(projectRepository).save(project);
    }

    @Test
    void deleteProject_WhenValidRequest_ShouldDeleteProjectResponseDto() {
        Mockito.when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        projectService.deleteProjectById(1L);


        verify(projectRepository).findById(1L);
        verify(projectRepository).delete(any(Project.class));
        verify(authorizationService).validateAdmin();
    }

    @Test
    void getAllProjects_WhenValidRequest_ShouldReturnAllProjectsResponseDto() {
        Mockito.when(projectRepository.findAll()).thenReturn(List.of(project));
        Mockito.when(projectMapper.toDto(project)).thenReturn(responseDto);

        List<ProjectResponseDto> result = projectService.getAllProjects();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(responseDto, result.get(0));
        verify(projectRepository).findAll();
        verify(projectMapper).toDto(project);
    }

    @Test
    void getProjectById_WhenValidRequest_ShouldReturnProjectResponseDto() {
        Mockito.when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));
        Mockito.when(projectMapper.toDto(project)).thenReturn(responseDto);

        ProjectResponseDto found = projectService.findProjectById(1L);

        verify(projectRepository).findById(1L);
        verify(projectMapper).toDto(project);
        assertEquals(responseDto, found);
    }

    @Test
    void changeProjectStatus_WhenValidRequest_ShouldSaveUpdatedProjectStatus() {
        Mockito.when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));

        projectService.changeProjectStatus(1L, new ChangeProjectStatusDto(ProjectStatus.COMPLETED));

        verify(projectRepository).findById(1L);
        verify(projectRepository).save(project);
        verify(authorizationService).validateProjectManagerOrAdmin(1L);
        assertEquals(ProjectStatus.COMPLETED, project.getProjectStatus());
    }

    @Test
    void deleteProjectById_WhenProjectNotFound_ShouldThrowException() {
        Mockito.when(projectRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, ()
                -> projectService.deleteProjectById(2L));
        verify(projectRepository).findById(2L);
        verify(projectRepository, never()).delete(any(Project.class));

    }

    @Test
    void getProjectById_WhenProjectNotFound_ShouldThrowException() {
        Mockito.when(projectRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, ()
                -> projectService.findProjectById(2L));
        verify(projectRepository).findById(2L);
    }

    @Test
    void changeProjectStatus_WhenNoStateChange_ShouldThrowException() {
        Mockito.when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));


        assertThrows(NoStateChangeException.class, ()
                -> projectService.changeProjectStatus(project.getProjectId(), new ChangeProjectStatusDto(ProjectStatus.PLANNED)));
        verify(projectRepository).findById(1L);
        verify(authorizationService).validateProjectManagerOrAdmin(1L);
        verify(projectRepository, never()).save(any());
    }

    @Test
    void changeProjectStatus_WhenProjectNotFound_ShouldThrowException() {
        Mockito.when(projectRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, ()
                -> projectService.changeProjectStatus(2L, new ChangeProjectStatusDto(ProjectStatus.PLANNED)));
        verify(projectRepository).findById(2L);
        verify(projectRepository, never()).save(any());

    }

    @Test
    void updateProject_WhenProjectNotFound_ShouldThrowException() {
        Mockito.when(projectRepository.findById(2L))
                .thenReturn(Optional.empty());

        ProjectRequestDto requestDto = new ProjectRequestDto("Test", "Test");

        assertThrows(ResourceNotFoundException.class, ()
                -> projectService.updateProject(requestDto, 2L));
        verify(projectRepository).findById(2L);
        verify(projectRepository, never()).save(any());
    }


}
