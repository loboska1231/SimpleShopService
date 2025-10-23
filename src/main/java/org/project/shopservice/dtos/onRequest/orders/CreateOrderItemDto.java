package org.project.shopservice.dtos.onRequest.orders;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record CreateOrderItemDto(
		@NotBlank(message = "Product Id is empty!")
		@Length(min = 1) String productId,

        @NotNull(message = "Amount is empty!") @Min(value = 1L, message = "amount should be greater than 1!")
        Long amount
){
}
