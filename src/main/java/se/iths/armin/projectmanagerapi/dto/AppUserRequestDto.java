package se.iths.armin.projectmanagerapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AppUserRequestDto(

        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
                message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,
        @NotBlank
        String password,
        @NotBlank
        String firstname,
        @NotBlank
        String lastname
) {
}
