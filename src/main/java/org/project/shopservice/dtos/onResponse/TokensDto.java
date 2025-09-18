package org.project.shopservice.dtos.onResponse;

import lombok.Builder;

@Builder
public record TokensDto(
        String accessToken,
        String refreshToken
) {
}
