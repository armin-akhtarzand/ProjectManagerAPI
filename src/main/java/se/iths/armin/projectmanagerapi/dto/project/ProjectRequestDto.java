package se.iths.armin.projectmanagerapi.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectRequestDto(
        @NotBlank(message = "Title is required")
        @Size(max = 50, message = "Title can not be longer than 50 characters")
        String title,
        @NotBlank(message = "Description is required")
        @Size(max = 1000, message = "Description can not be longer than 1000 characters")
        String description
) {
}
