package org.project.shopservice.dtos.onRequest.products;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Builder
public record UpdateProductDto(
        @Nullable @Min(value = 1) BigDecimal price,
        @Nullable @Length(min=1) String category,
        @Nullable @Length(min=1) String type
) {
}
