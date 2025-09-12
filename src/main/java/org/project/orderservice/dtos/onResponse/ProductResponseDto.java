package org.project.orderservice.dtos.onResponse;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductResponseDto(
        Integer id,
        String category,
        BigDecimal price,
        String type,
        Integer amount
) {
}
