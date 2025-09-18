package org.project.shopservice.dtos.onResponse;

import lombok.Builder;

@Builder
public record OrderItemResponseDto(
        String productId,
        Long amount
) {
}
