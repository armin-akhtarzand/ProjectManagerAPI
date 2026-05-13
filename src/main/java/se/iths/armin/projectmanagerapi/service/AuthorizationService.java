package se.iths.armin.projectmanagerapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import se.iths.armin.projectmanagerapi.entity.AppUser;
import se.iths.armin.projectmanagerapi.entity.Project;
import se.iths.armin.projectmanagerapi.entity.enums.ProjectRole;
import se.iths.armin.projectmanagerapi.entity.enums.UserPosition;
import se.iths.armin.projectmanagerapi.exception.ResourceNotFoundException;
import se.iths.armin.projectmanagerapi.exception.UnauthorizedException;
import se.iths.armin.projectmanagerapi.repository.AppUserRepository;
import se.iths.armin.projectmanagerapi.repository.ProjectRepository;
import se.iths.armin.projectmanagerapi.repository.ProjectUserRepository;

@Service
@RequiredArgsConstructor
public class AuthorizationService {


    private final AppUserRepository appUserRepository;
    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;

    public AppUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    public void validateSelfOrAdmin(Long id) {
        AppUser currentUser = getCurrentUser();

        boolean isSelf = currentUser.getUserid().equals(id);
        boolean isAdmin = currentUser.getUserPosition().equals(UserPosition.ADMIN);
        if (!isSelf && !isAdmin) {
            throw new UnauthorizedException("You are not allowed to perform this action");
        }
    }

    public void validateAdmin() {

        AppUser currentUser = getCurrentUser();
        boolean isAdmin = currentUser.getUserPosition().equals(UserPosition.ADMIN);
        if (!isAdmin) {
            throw new UnauthorizedException("You are not allowed to perform this action");
        }
    }

    public void validateProjectManagerOrAdmin(Long projectId) {
        AppUser currentUser = getCurrentUser();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        boolean isProjectManager = projectUserRepository
                .existsByAppUserAndProjectAndProjectRole(currentUser, project, ProjectRole.PROJECT_MANAGER);
        boolean isAdmin = currentUser.getUserPosition().equals(UserPosition.ADMIN);

        if (!isProjectManager && !isAdmin) {
            throw new UnauthorizedException("You are not allowed to perform this action");
        }
    }

    public void validateSelf(Long id) {
        AppUser currentUser = getCurrentUser();
        boolean isSelf = currentUser.getUserid().equals(id);

        if (!isSelf) {
            throw new UnauthorizedException("You are not allowed to perform this action");
        }
    }

    public void validateSelfAdminOrProjectManager(Long userId, Long projectId) {
        AppUser currentUser = getCurrentUser();

        boolean isSelf = currentUser.getUserid().equals(userId);
        boolean isAdmin = currentUser.getUserPosition().equals(UserPosition.ADMIN);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        boolean isProjectManager = projectUserRepository
                .existsByAppUserAndProjectAndProjectRole(currentUser, project, ProjectRole.PROJECT_MANAGER);

        if (!isSelf && !isAdmin && !isProjectManager) {
            throw new UnauthorizedException("You are not allowed to perform this action");
        }
    }

}
