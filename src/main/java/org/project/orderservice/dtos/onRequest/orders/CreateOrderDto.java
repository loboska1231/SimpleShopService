package org.project.orderservice.dtos.onRequest.orders;

import jakarta.validation.constraints.Email;
import lombok.Builder;

import java.util.List;

@Builder
public record CreateOrderDto(
        String address,
        List<OrderProductDefDto> products
) {
}
