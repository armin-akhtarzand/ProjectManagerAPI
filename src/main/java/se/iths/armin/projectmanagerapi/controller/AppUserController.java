package se.iths.armin.projectmanagerapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.iths.armin.projectmanagerapi.dto.appuser.AppUserRequestDto;
import se.iths.armin.projectmanagerapi.dto.appuser.AppUserResponseDto;
import se.iths.armin.projectmanagerapi.dto.appuser.ChangePasswordDto;
import se.iths.armin.projectmanagerapi.service.AppUserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/appusers")
public class AppUserController {

    private final AppUserService appUserService;


    @GetMapping()
    public ResponseEntity<List<AppUserResponseDto>> getAllAppUsers() {
        List<AppUserResponseDto> users = appUserService.findAllAppUsers();

        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        AppUserResponseDto user = appUserService.findById(id);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAppUser(@RequestBody @Valid AppUserRequestDto appUserRequestDto) {
        AppUserResponseDto user = appUserService.createAppUser(appUserRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@RequestBody @Valid AppUserRequestDto appUserRequestDto,
                                    @PathVariable Long id) {

        AppUserResponseDto updatedUser = appUserService.updateAppUser(id, appUserRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    @PatchMapping("/update/password/{id}")
    public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordDto changePasswordDto,
                                            @PathVariable Long id) {
        AppUserResponseDto existingUser = appUserService.findById(id);

        appUserService.changePassword(id, changePasswordDto);

        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        appUserService.deleteAppUser(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
