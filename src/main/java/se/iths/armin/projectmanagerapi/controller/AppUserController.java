package se.iths.armin.projectmanagerapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.iths.armin.projectmanagerapi.dto.AppUserRequestDto;
import se.iths.armin.projectmanagerapi.dto.AppUserResponseDto;
import se.iths.armin.projectmanagerapi.service.AppUserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class AppUserController {


    private final AppUserService appUserService;

    @PostMapping
    public AppUserResponseDto createAppUser(@RequestBody @Valid AppUserRequestDto appUserRequestDto) {
        return appUserService.createAppUser(appUserRequestDto);
    }
}
