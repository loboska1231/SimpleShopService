package org.project.orderservice.dtos.onResponse;

public record TokensDto(
        String accessToken,
        String refreshToken
) {
}
