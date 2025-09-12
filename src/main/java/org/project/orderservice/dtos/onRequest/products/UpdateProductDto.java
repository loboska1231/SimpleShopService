package org.project.orderservice.dtos.onRequest.products;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.NonNull;


import java.math.BigDecimal;

@Builder
public record UpdateProductDto(
        @NonNull @NotBlank Integer id,
        @Nullable @Min(value = 1) BigDecimal price
) {
}
