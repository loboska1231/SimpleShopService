package org.project.shopservice.dtos.onRequest.orders;

import lombok.Builder;

@Builder
public record UpdateOrderItemDto(
        String productId,
        Long amount
) {
}
