package se.iths.armin.projectmanagerapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import se.iths.armin.projectmanagerapi.dto.appuser.*;
import se.iths.armin.projectmanagerapi.entity.AppUser;
import se.iths.armin.projectmanagerapi.entity.enums.UserPosition;
import se.iths.armin.projectmanagerapi.entity.enums.UserStatus;
import se.iths.armin.projectmanagerapi.exception.*;
import se.iths.armin.projectmanagerapi.mapper.AppUserMapperImpl;
import se.iths.armin.projectmanagerapi.repository.AppUserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private AppUserMapperImpl appUserMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private AppUserService appUserService;

    private AppUserRequestDto requestDto;
    private AppUser appUser;
    private AppUserResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new AppUserRequestDto(
                "test@test.com", "testpw", "firstname", "lastname"
        );

        appUser = new AppUser();
        appUser.setEmail("test@test.com");
        appUser.setPassword("testpw");
        appUser.setFirstname("firstname");
        appUser.setLastname("lastname");
        appUser.setUserid(1L);

        responseDto = new AppUserResponseDto(
                appUser.getEmail(),
                appUser.getFirstname(),
                appUser.getLastname(),
                appUser.getUserPosition(),
                appUser.getUserStatus()
        );
    }


    @Test
    void createAppUser_WhenValidRequest_ShouldReturnAppUserResponseDto() {

        Mockito.when(appUserRepository.existsByEmail("test@test.com")).thenReturn(false);
        Mockito.when(passwordEncoder.encode("testpw")).thenReturn("testpw");

        Mockito.when(appUserMapper.toEntity(requestDto)).thenReturn(appUser);
        Mockito.when(appUserRepository.save(Mockito.any(AppUser.class))).thenReturn(appUser);

        Mockito.when(appUserMapper.toDto(appUser)).thenReturn(responseDto);

        AppUserResponseDto result = appUserService.createAppUser(requestDto);

        assertNotNull(result);
        assertEquals(appUser.getFirstname(), result.firstname());
        assertEquals(responseDto, result);
        verify(authorizationService).validateAdmin();
    }

    @Test
    void updateAppUser_WhenValidEmail_ShouldReturnAppUserResponseDto() {
        AppUserUpdateDto appUserUpdateDto = new AppUserUpdateDto("example@example.com", "firstname", "lastname");
        Mockito.when(appUserRepository.findById(1L))

                .thenReturn(Optional.of(appUser));
        Mockito.doNothing()
                .when(appUserMapper).updateEntity(appUser, appUserUpdateDto);

        AppUserResponseDto updated = new AppUserResponseDto("example@example.com",
                appUser.getFirstname(), appUser.getLastname(), appUser.getUserPosition(), appUser.getUserStatus());
        Mockito.when(appUserRepository.save(appUser)).thenReturn(appUser);
        Mockito.when(appUserMapper.toDto(appUser)).thenReturn(updated);


        Mockito.when(appUserRepository.existsByEmail("example@example.com")).thenReturn(false);
        AppUserResponseDto updateResponse = appUserService.updateAppUser(appUser.getUserid(), appUserUpdateDto);


        assertEquals("example@example.com", updateResponse.email());

        verify(appUserRepository).findById(1L);
        verify(appUserRepository).existsByEmail("example@example.com");
        verify(appUserRepository).save(appUser);
        verify(authorizationService).validateSelfOrAdmin(1L);
    }

    @Test
    void deleteAppUser_WhenUserExists_ShouldDeleteAppUser() {
        Mockito.when(appUserRepository.findById(1L)).thenReturn(Optional.of(appUser));


        appUserService.deleteAppUser(1L);


        Mockito.verify(appUserRepository).delete(Mockito.any(AppUser.class));
        verify(appUserRepository).findById(1L);
        verify(authorizationService).validateAdmin();
    }

    @Test
    void getAppUser_WhenValidRequest_ShouldReturnAppUser() {
        Mockito.when(appUserRepository.findById(1L)).thenReturn(Optional.of(appUser));

        AppUser result = appUserService.getAppUser(1L);

        Mockito.verify(appUserRepository).findById(1L);
        assertEquals(appUser, result);
    }

    @Test
    void findAppUserById_WhenValidRequest_ShouldReturnAppUserResponseDto() {
        Mockito.when(appUserRepository.findById(1L)).thenReturn(Optional.of(appUser));

        Mockito.when(appUserMapper.toDto(appUser)).thenReturn(responseDto);

        AppUserResponseDto foundResponse = appUserService.findById(1L);


        verify(appUserRepository).findById(1L);
        verify(appUserMapper).toDto(appUser);
        assertEquals(responseDto, foundResponse);

    }

    @Test
    void getAllAppUsers_WhenUsersExist_ShouldReturnAllAppUserResponseDto() {
        Mockito.when(appUserRepository.findAll()).thenReturn(List.of(appUser));
        Mockito.when(appUserMapper.toDto(appUser)).thenReturn(responseDto);

        List<AppUserResponseDto> result = appUserService.findAllAppUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(responseDto, result.get(0));
        verify(appUserRepository).findAll();
        verify(appUserMapper).toDto(appUser);
    }

    @Test
    void changePassword_WhenValidRequest_ShouldChangePassword() {
        Mockito.when(appUserRepository.findById(1L)).thenReturn(Optional.of(appUser));
        Mockito.when(passwordEncoder.matches("testpw", "testpw")).thenReturn(true);
        Mockito.when(passwordEncoder.encode("newpassword")).thenReturn("newpassword");

        ChangePasswordDto changePasswordDto = new ChangePasswordDto("newpassword", "testpw");


        appUserService.changePassword(appUser.getUserid(), changePasswordDto);

        Mockito.verify(appUserRepository).findById(1L);
        Mockito.verify(appUserRepository).save(Mockito.any(AppUser.class));
        assertEquals("newpassword", appUser.getPassword());
        verify(authorizationService).validateSelf(1L);


    }

    @Test
    void changePosition_WhenValidRequest_ShouldChangePosition() {
        Mockito.when(appUserRepository.findById(1L)).thenReturn(Optional.of(appUser));
        Mockito.when(appUserRepository.save(Mockito.any(AppUser.class))).thenReturn(appUser);

        ChangeAppUserPositionDto changeAppUserPositionDto = new ChangeAppUserPositionDto(UserPosition.ADMIN);

        appUserService.changeUserPosition(appUser.getUserid(), changeAppUserPositionDto);

        Mockito.verify(appUserRepository).findById(1L);
        Mockito.verify(appUserRepository).save(Mockito.any(AppUser.class));
        assertEquals(UserPosition.ADMIN, appUser.getUserPosition());
        verify(authorizationService).validateAdmin();

    }

    @Test
    void changeStatus_WhenValidRequest_ShouldChangeStatus() {
        Mockito.when(appUserRepository.findById(1L)).thenReturn(Optional.of(appUser));
        Mockito.when(appUserRepository.save(Mockito.any(AppUser.class))).thenReturn(appUser);
        appUser.setUserPosition(UserPosition.ADMIN);
        Mockito.when(authorizationService.getCurrentUser()).thenReturn(appUser);

        ChangeAppUserStatusDto changeAppUserStatusDto = new ChangeAppUserStatusDto(UserStatus.VACATION);

        appUserService.changeUserStatus(appUser.getUserid(), changeAppUserStatusDto);

        Mockito.verify(appUserRepository).findById(1L);
        Mockito.verify(appUserRepository).save(Mockito.any(AppUser.class));
        assertEquals(UserStatus.VACATION, appUser.getUserStatus());
        verify(authorizationService).validateSelfOrAdmin(1L);
    }


    @Test
    void createAppUser_WhenDuplicateEmail_ShouldThrowException() {
        AppUserRequestDto duplicateDto = new AppUserRequestDto(
                "test@test.com", "testpw", "firstname", "lastname"
        );

        Mockito.when(appUserRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertThrows(DuplicateFoundException.class, () -> appUserService.createAppUser(duplicateDto));
    }

    @Test
    void findAppUserById_AppUserNotFound_ShouldThrowException() {
        Mockito.when(appUserRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> appUserService.findById(2L));
    }

    @Test
    void updateAppUser_WhenDuplicateEmail_ShouldThrowException() {
        AppUserUpdateDto duplicateUpdateDto = new AppUserUpdateDto(
                "example@example.com", "firstname", "lastname"
        );
        Mockito.when(appUserRepository.findById(1L)).thenReturn(Optional.of(appUser));

        Mockito.when(appUserRepository.existsByEmail("example@example.com")).thenReturn(true);

        assertThrows(DuplicateFoundException.class, () -> appUserService.updateAppUser(1L, duplicateUpdateDto));

    }

    @Test
    void deleteAppUserById_AppUserNotFound_ShouldThrowException() {
        Mockito.when(appUserRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> appUserService.deleteAppUser(1L));
    }

    @Test
    void changePassword_InvalidCredentials_ShouldThrowException() {
        Mockito.when(appUserRepository.findById(1L)).thenReturn(Optional.of(appUser));

        ChangePasswordDto changePasswordDto = new ChangePasswordDto("wrongpassword", "testpw");


        assertThrows(InvalidPasswordException.class, () -> appUserService.changePassword(appUser.getUserid(), changePasswordDto));
    }

    @Test
    void changePosition_NoStateChange_ShouldThrowException() {
        Mockito.when(appUserRepository.findById(1L)).thenReturn(Optional.of(appUser));

        ChangeAppUserPositionDto changeAppUserPositionDto = new ChangeAppUserPositionDto(UserPosition.EMPLOYEE);

        assertThrows(NoStateChangeException.class, () -> appUserService.changeUserPosition(appUser.getUserid(), changeAppUserPositionDto));
    }

    @Test
    void changeStatus_NoStateChange_ShouldThrowException() {
        Mockito.when(appUserRepository.findById(1L)).thenReturn(Optional.of(appUser));
        Mockito.when(authorizationService.getCurrentUser()).thenReturn(appUser);

        ChangeAppUserStatusDto changeAppUserStatusDto = new ChangeAppUserStatusDto(UserStatus.ACTIVE);

        assertThrows(NoStateChangeException.class, () -> appUserService.changeUserStatus(appUser.getUserid(), changeAppUserStatusDto));
    }

    @Test
    void changeStatus_NotAdmin_ShouldThrowException() {
        Mockito.when(appUserRepository.findById(1L)).thenReturn(Optional.of(appUser));
        Mockito.when(authorizationService.getCurrentUser()).thenReturn(appUser);

        ChangeAppUserStatusDto changeAppUserStatusDto = new ChangeAppUserStatusDto(UserStatus.INACTIVE);

        assertThrows(ForbiddenRequestException.class, () -> appUserService.changeUserStatus(appUser.getUserid(), changeAppUserStatusDto));
    }


}
