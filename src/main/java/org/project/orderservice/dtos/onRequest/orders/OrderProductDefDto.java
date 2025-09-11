package org.project.orderservice.dtos.onRequest.orders;

import lombok.Builder;

@Builder
public record OrderProductDefDto(
        String productId,
        Integer amount
) {
}
