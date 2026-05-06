package se.iths.armin.projectmanagerapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import se.iths.armin.projectmanagerapi.dto.AppUserRequestDto;
import se.iths.armin.projectmanagerapi.dto.AppUserResponseDto;
import se.iths.armin.projectmanagerapi.dto.ChangePasswordDto;
import se.iths.armin.projectmanagerapi.entity.AppUser;
import se.iths.armin.projectmanagerapi.exception.DuplicateFoundException;
import se.iths.armin.projectmanagerapi.exception.InvalidPasswordException;
import se.iths.armin.projectmanagerapi.exception.ResourceNotFoundException;
import se.iths.armin.projectmanagerapi.mapper.EntityMapper;
import se.iths.armin.projectmanagerapi.repository.AppUserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository appUserRepository;
    private final EntityMapper<AppUser, AppUserRequestDto, AppUserResponseDto> appUserMapper;

    public AppUserResponseDto createAppUser(AppUserRequestDto appUserRequestDto) {
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

    private AppUser getAppUser(Long id) {
        AppUser appUser = appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return appUser;
    }

    public AppUserResponseDto updateAppUser(Long id, AppUserRequestDto appUserRequestDto) {
        AppUser appUser = getAppUser(id);

        if (!appUser.getEmail().equals(appUserRequestDto.email())) {
            if (appUserRepository.existsByEmail(appUserRequestDto.email())) {
                throw new DuplicateFoundException("Email already exists");
            }
        }
        appUserMapper.updateEntity(appUser, appUserRequestDto);

        AppUser saved = appUserRepository.save(appUser);

        return appUserMapper.toDto(saved);
    }

    public void deleteAppUser(Long id) {
        AppUser appUser = getAppUser(id);

        appUserRepository.delete(appUser);
    }

    public void changePassword(AppUserRequestDto appUserRequestDto) {
    }

    public void changePassword(String oldPassword, Long id, ChangePasswordDto changePasswordDto) {
        AppUser appUser = getAppUser(id);
        if (!passwordEncoder.matches(changePasswordDto.oldPassword(), appUser.getPassword())) {
            throw new InvalidPasswordException("Old password is not correct");
        }
        appUser.setPassword(passwordEncoder.encode(changePasswordDto.newPassword()));

        appUserRepository.save(appUser);
    }

}
