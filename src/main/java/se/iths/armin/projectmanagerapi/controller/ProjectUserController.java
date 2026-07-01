package se.iths.armin.projectmanagerapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.iths.armin.projectmanagerapi.dto.projectuser.ProjectUserRequestDto;
import se.iths.armin.projectmanagerapi.service.ProjectUserService;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectUserController {

    ProjectUserService projectUserService;


    @PostMapping("/{projectId/members}")
    public ResponseEntity<ProjectUserRequestDto> addUserToProject(
            @PathVariable Long projectId,
            @RequestBody @Valid ProjectUserRequestDto projectUserRequestDto) {

    }


}
