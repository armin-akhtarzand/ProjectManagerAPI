package se.iths.armin.projectmanagerapi.dto.project;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectRequestDto(
        @NotBlank(message = "Title is required")
        @Max(value = 25, message = "Title can not be longer than 25 characters")
        String title,
        @NotBlank(message = "Description is required")
        @Size(max = 1000, message = "Description can not be longer than 1000 characters")
        String description
) {
}
