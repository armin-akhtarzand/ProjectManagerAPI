package se.iths.armin.projectmanagerapi.mapper;

import org.springframework.stereotype.Component;
import se.iths.armin.projectmanagerapi.dto.AppUserRequestDto;
import se.iths.armin.projectmanagerapi.dto.AppUserRespondDto;
import se.iths.armin.projectmanagerapi.dto.ChangePasswordDto;
import se.iths.armin.projectmanagerapi.entity.AppUser;

@Component
public class AppUserMapperImpl
        implements EntityMapper<AppUser, AppUserRequestDto, AppUserRespondDto> {

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
    public AppUserRespondDto toDto(AppUser appUser) {
        if (appUser == null) {
            return null;
        }
        AppUserRespondDto userRespond = new AppUserRespondDto
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

    public void changePassword(AppUser appUser, ChangePasswordDto changePasswordDto) {
        if (changePasswordDto == null) {
            return;
        }
        appUser.setPassword(changePasswordDto.password());
    }
}
