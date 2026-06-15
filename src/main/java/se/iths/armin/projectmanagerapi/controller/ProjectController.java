package se.iths.armin.projectmanagerapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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


}
