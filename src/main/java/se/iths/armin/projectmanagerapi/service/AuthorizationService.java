package se.iths.armin.projectmanagerapi;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import se.iths.armin.projectmanagerapi.entity.AppUser;
import se.iths.armin.projectmanagerapi.entity.enums.UserPosition;
import se.iths.armin.projectmanagerapi.exception.ResourceNotFoundException;
import se.iths.armin.projectmanagerapi.exception.UnauthorizedException;
import se.iths.armin.projectmanagerapi.repository.AppUserRepository;

@Service
@RequiredArgsConstructor
public class AuthorizationService {


    private final AppUserRepository appUserRepository;

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

    public void validateManagerOrAdmin() {
        AppUser currentUser = getCurrentUser();
        boolean isManager = currentUser.getUserPosition().equals(UserPosition.MANAGER);
        boolean isAdmin = currentUser.getUserPosition().equals(UserPosition.ADMIN);

        if (!isManager && !isAdmin) {
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

}
