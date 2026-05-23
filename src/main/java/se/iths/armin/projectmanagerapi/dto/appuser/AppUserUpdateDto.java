package se.iths.armin.projectmanagerapi.dto.appuser;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AppUserUpdateDto(
        @NotBlank(message = "Email is required")
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                message = "Invalid email format")
        String email,
        @NotBlank(message = "Firstname is required")
        String firstname,
        @NotBlank(message = "Lastname is required")
        String lastname
) {
}
