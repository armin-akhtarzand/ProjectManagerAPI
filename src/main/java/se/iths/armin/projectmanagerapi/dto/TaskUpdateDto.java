package se.iths.armin.projectmanagerapi.dto;

import java.time.LocalDate;

public record TaskUpdateDto(
        String title,
        String description,
        LocalDate deadline
) {
}
