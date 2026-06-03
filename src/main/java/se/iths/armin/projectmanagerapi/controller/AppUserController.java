package se.iths.armin.projectmanagerapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.iths.armin.projectmanagerapi.dto.appuser.*;
import se.iths.armin.projectmanagerapi.service.AppUserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class AppUserController {

    private final AppUserService appUserService;


    @GetMapping()
    public ResponseEntity<List<AppUserResponseDto>> getAllAppUsers() {
        List<AppUserResponseDto> users = appUserService.findAllAppUsers();

        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppUserResponseDto> findById(@PathVariable Long id) {
        AppUserResponseDto user = appUserService.findById(id);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping
    public ResponseEntity<AppUserResponseDto> createAppUser(@RequestBody @Valid AppUserRequestDto appUserRequestDto) {
        AppUserResponseDto user = appUserService.createAppUser(appUserRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppUserResponseDto> update(@RequestBody @Valid AppUserUpdateDto appUserUpdateDto,
                                                     @PathVariable Long id) {

        AppUserResponseDto updatedUser = appUserService.updateAppUser(id, appUserUpdateDto);

        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordDto changePasswordDto,
                                               @PathVariable Long id) {

        appUserService.changePassword(id, changePasswordDto);

        return ResponseEntity.noContent().build();

    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(@RequestBody @Valid ChangeAppUserStatusDto changeAppUserStatusDto,
                                             @PathVariable Long id) {
        appUserService.changeUserStatus(id, changeAppUserStatusDto);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/position")
    public ResponseEntity<Void> changePosition(@RequestBody @Valid ChangeAppUserPositionDto changeAppUserPositionDto,
                                               @PathVariable Long id) {

        appUserService.changeUserPosition(id, changeAppUserPositionDto);

        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        appUserService.deleteAppUser(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
