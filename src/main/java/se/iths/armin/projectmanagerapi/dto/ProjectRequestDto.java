package se.iths.armin.projectmanagerapi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectRequestDto(
        @NotBlank
        @Max(value = 25, message = "Title can not be longer than 25 characters")
        String title,
        @NotBlank
        @Size(max = 1000, message = "Description can not be longer than 1000 characters")
        String description
) {
}
