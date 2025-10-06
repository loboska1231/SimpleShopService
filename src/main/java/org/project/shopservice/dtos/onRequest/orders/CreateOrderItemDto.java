package org.project.shopservice.dtos.onRequest.orders;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.validator.constraints.Length;
import org.apache.commons.lang3.StringUtils;

@Builder
public record CreateOrderItemDto(
        @NotNull @NotBlank @Length(min = 1) String productId,
        @NotNull @Min(value = 1) Long amount
){
}
