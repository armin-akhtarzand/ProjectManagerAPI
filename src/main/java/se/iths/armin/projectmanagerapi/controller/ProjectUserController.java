package se.iths.armin.projectmanagerapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.iths.armin.projectmanagerapi.dto.projectuser.*;
import se.iths.armin.projectmanagerapi.service.ProjectUserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProjectUserController {

    private final ProjectUserService projectUserService;


    @PostMapping("/projects/members")
    public ResponseEntity<ProjectUserResponseDto> addUserToProject(
            @RequestBody @Valid ProjectUserRequestDto projectUserRequestDto) {


        ProjectUserResponseDto responseDto =
                projectUserService.addUserToProject(projectUserRequestDto);


        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/projects/{projectId}/members")
    public ResponseEntity<List<ProjectMemberResponseDto>> getAllProjectsMembers(
            @PathVariable Long projectId) {

        List<ProjectMemberResponseDto> responseDtoList =
                projectUserService.getAllUsersFromProject(projectId);

        return ResponseEntity.status(HttpStatus.OK).body(responseDtoList);
    }

    @GetMapping("/users/{userId}/projects")
    public ResponseEntity<List<UserProjectResponseDto>> getAllProjectFromUser(
            @PathVariable Long userId) {

        List<UserProjectResponseDto> responseDtoList =
                projectUserService.getAllProjectsFromUser(userId);

        return ResponseEntity.status(HttpStatus.OK).body(responseDtoList);
    }

    @PatchMapping("/projects/members/role")
    public ResponseEntity<Void> changeProjectRole(
            @RequestBody @Valid ChangeProjectRoleDto changeProjectRoleDto) {
        projectUserService.changeProjectRole(changeProjectRoleDto);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/projects/members")
    public ResponseEntity<Void> removeUserFromProject(
            @RequestBody @Valid ProjectUserRequestDto projectUserRequestDto
    ) {

        projectUserService.removeUserFromProject(projectUserRequestDto);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
