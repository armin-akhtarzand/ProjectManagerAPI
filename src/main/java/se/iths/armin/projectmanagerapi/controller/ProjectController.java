package se.iths.armin.projectmanagerapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.iths.armin.projectmanagerapi.dto.project.ChangeProjectStatusDto;
import se.iths.armin.projectmanagerapi.dto.project.ProjectRequestDto;
import se.iths.armin.projectmanagerapi.dto.project.ProjectResponseDto;
import se.iths.armin.projectmanagerapi.service.ProjectService;

import java.util.List;

@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectResponseDto>> getAllProjects() {
        List<ProjectResponseDto> responseDto = projectService.getAllProjects();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> getProjectById(@PathVariable Long id) {
        ProjectResponseDto responseDto = projectService.findProjectById(id);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDto> saveProject(@RequestBody ProjectRequestDto projectRequestDto) {
        ProjectResponseDto project = projectService.createProject(projectRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(project);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> updateProject(@RequestBody ProjectRequestDto projectRequestDto,
                                                            @PathVariable Long id) {
        ProjectResponseDto project = projectService.updateProject(projectRequestDto, id);

        return ResponseEntity.status(HttpStatus.OK).body(project);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> patchProjectStatus(@RequestBody ChangeProjectStatusDto changeProjectStatusDto,
                                                   @PathVariable Long id) {
        projectService.changeProjectStatus(id, changeProjectStatusDto);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProjectById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
