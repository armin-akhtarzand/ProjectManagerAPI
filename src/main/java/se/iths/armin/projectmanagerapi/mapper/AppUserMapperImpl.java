package se.iths.armin.projectmanagerapi.mapper;

import org.springframework.stereotype.Component;
import se.iths.armin.projectmanagerapi.dto.AppUserRequestDto;
import se.iths.armin.projectmanagerapi.dto.AppUserResponseDto;
import se.iths.armin.projectmanagerapi.entity.AppUser;

@Component
public class AppUserMapperImpl
        implements EntityMapper<AppUser, AppUserRequestDto, AppUserResponseDto> {

    @Override
    public AppUser toEntity(AppUserRequestDto appUserRequestDto) {
        if (appUserRequestDto == null) {
            return null;
        }
        AppUser appUser = new AppUser();
        appUser.setFirstname(appUserRequestDto.firstname());
        appUser.setLastname(appUserRequestDto.lastname());
        appUser.setEmail(appUserRequestDto.email());
        appUser.setPassword(appUserRequestDto.password());

        return appUser;
    }

    @Override
    public AppUserResponseDto toDto(AppUser appUser) {
        if (appUser == null) {
            return null;
        }
        AppUserResponseDto userRespond = new AppUserResponseDto
                (appUser.getEmail(), appUser.getFirstname(), appUser.getLastname(), appUser.getUserPosition(), appUser.getUserStatus());

        return userRespond;
    }

    @Override
    public void updateEntity(AppUser appUser, AppUserRequestDto appUserRequestDto) {
        if (appUserRequestDto == null) {
            return;
        }
        appUser.setFirstname(appUserRequestDto.firstname());
        appUser.setLastname(appUserRequestDto.lastname());
        appUser.setEmail(appUserRequestDto.email());
    }
}
