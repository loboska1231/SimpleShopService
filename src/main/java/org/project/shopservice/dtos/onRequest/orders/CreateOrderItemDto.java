package org.project.shopservice.dtos.onRequest.orders;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record CreateOrderItemDto(
        @NotNull @NotBlank @Length(min = 1) String productId,
        @NotNull @Min(value = 1) Long amount
) {
}
