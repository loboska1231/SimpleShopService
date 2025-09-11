package org.project.orderservice.dtos.onRequest.products;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import org.project.orderservice.enums.Currency;

import java.math.BigDecimal;

@Builder
public record UpdateProductDto(
        @Nullable @Min(value = 1) BigDecimal price,
        Currency currency
) {
}
