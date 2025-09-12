package org.project.orderservice.dtos.onResponse;

import lombok.Builder;
import org.project.orderservice.enums.Currency;

import java.math.BigDecimal;

@Builder
public record ProductResponseDto(
        String id,
        String category,
        BigDecimal price,
        Currency currency,
        String type,
        String isAvailable,
        Integer amount
) {
}
