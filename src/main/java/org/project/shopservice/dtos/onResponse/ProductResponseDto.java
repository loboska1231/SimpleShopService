package org.project.shopservice.dtos.onResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductResponseDto(
        String id,
        String category,
        BigDecimal price,
        String type
) {
}
