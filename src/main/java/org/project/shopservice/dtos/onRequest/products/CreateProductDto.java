package org.project.shopservice.dtos.onRequest.products;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Builder
public record   CreateProductDto(
		@NonNull @NotBlank @Length(min = 1, max = 30) String category,
		@NonNull @DecimalMin(value = "1") BigDecimal price,
        @NonNull @NotBlank @Length(min = 1, max = 30) String type
) {
}
