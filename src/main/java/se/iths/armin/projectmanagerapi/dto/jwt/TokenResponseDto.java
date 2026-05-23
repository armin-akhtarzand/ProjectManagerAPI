package se.iths.armin.projectmanagerapi.dto.jwt;

import java.util.List;

public record TokenResponseDto(
        String accessToken,
        long expiresIn,
        String subject,
        List<String> roles
) {
}
