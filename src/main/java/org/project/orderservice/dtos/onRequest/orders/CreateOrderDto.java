package org.project.orderservice.dtos.onRequest.orders;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;
import org.project.orderservice.dtos.onRequest.products.CreateProductDto;

import java.util.List;

@Builder
public record CreateOrderDto(
        @NonNull @NotBlank @Length(min = 1, max = 30) String address,
        @NonNull @NotBlank @Min(1) List<CreateProductDto> products
) {
}
