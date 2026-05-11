package se.iths.armin.projectmanagerapi.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.iths.armin.projectmanagerapi.dto.*;
import se.iths.armin.projectmanagerapi.entity.AppUser;
import se.iths.armin.projectmanagerapi.entity.enums.UserPosition;
import se.iths.armin.projectmanagerapi.entity.enums.UserStatus;
import se.iths.armin.projectmanagerapi.exception.*;
import se.iths.armin.projectmanagerapi.mapper.EntityMapper;
import se.iths.armin.projectmanagerapi.repository.AppUserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AuthorizationService authorizationService;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityMapper<AppUser, AppUserRequestDto, AppUserResponseDto> appUserMapper;

    @Transactional
    public AppUserResponseDto createAppUser(AppUserRequestDto appUserRequestDto) {

        authorizationService.validateAdmin();

        if (appUserRepository.existsByEmail(appUserRequestDto.email())) {
            throw new DuplicateFoundException("Email already exists");
        }

        AppUser appUser = appUserMapper.toEntity(appUserRequestDto);
        appUser.setPassword(passwordEncoder.encode(appUserRequestDto.password()));

        AppUser saved = appUserRepository.save(appUser);

        return appUserMapper.toDto(saved);
    }

    public List<AppUserResponseDto> findAllAppUsers() {
        List<AppUser> appUsers = appUserRepository.findAll();

        return appUsers.stream().map(appUserMapper::toDto).toList();
    }

    public AppUserResponseDto findById(Long id) {
        AppUser appUser = appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        AppUserResponseDto appUserResponseDto = appUserMapper.toDto(appUser);

        return appUserResponseDto;
    }

    public AppUser getAppUser(Long id) {
        AppUser appUser = appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return appUser;
    }

    @Transactional
    public AppUserResponseDto updateAppUser(Long id, AppUserRequestDto appUserRequestDto) {
        AppUser appUser = getAppUser(id);

        authorizationService.validateSelfOrAdmin(id);

        if (!appUser.getEmail().equals(appUserRequestDto.email())) {
            if (appUserRepository.existsByEmail(appUserRequestDto.email())) {
                throw new DuplicateFoundException("Email already exists");
            }
        }
        appUserMapper.updateEntity(appUser, appUserRequestDto);

        AppUser saved = appUserRepository.save(appUser);

        return appUserMapper.toDto(saved);
    }

    @Transactional
    public void deleteAppUser(Long id) {
        AppUser appUser = getAppUser(id);

        authorizationService.validateAdmin();

        appUserRepository.delete(appUser);
    }

    @Transactional
    public void changePassword(Long id, ChangePasswordDto changePasswordDto) {


        authorizationService.validateSelf(id);

        AppUser appUser = getAppUser(id);
        if (!passwordEncoder.matches(changePasswordDto.oldPassword(), appUser.getPassword())) {
            throw new InvalidPasswordException("Old password is not correct");
        }
        appUser.setPassword(passwordEncoder.encode(changePasswordDto.newPassword()));

        appUserRepository.save(appUser);
    }

    @Transactional
    public void changeUserPosition(Long id, ChangeAppUserPositionDto changeAppUserPositionDto) {
        AppUser appUser = getAppUser(id);

        authorizationService.validateAdmin();

        if (appUser.getUserPosition().equals(changeAppUserPositionDto.position())) {
            throw new NoStateChangeException("User already has this position");
        }
        appUser.setUserPosition(changeAppUserPositionDto.position());
        appUserRepository.save(appUser);
    }

    @Transactional
    public void changeUserStatus(Long id, ChangeAppUserStatusDto changeAppUserStatusDto) {
        AppUser appUser = getAppUser(id);

        authorizationService.validateSelfOrAdmin(id);

        boolean isAdmin = authorizationService.getCurrentUser().getUserPosition().equals(UserPosition.ADMIN);

        if (!isAdmin && changeAppUserStatusDto.status().equals(UserStatus.INACTIVE)) {
            throw new UnauthorizedException("Only admin can set status to INACTIVE");
        }

        if (appUser.getUserStatus().equals(changeAppUserStatusDto.status())) {
            throw new NoStateChangeException("User already has this status");
        }
        appUser.setUserStatus(changeAppUserStatusDto.status());
        appUserRepository.save(appUser);
    }


}
