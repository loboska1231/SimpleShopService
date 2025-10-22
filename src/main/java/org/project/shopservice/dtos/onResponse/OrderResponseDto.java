package org.project.shopservice.dtos.onResponse;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
@Builder(toBuilder = true)
public record OrderResponseDto (
		Long id,
        String whose,
        String email,
        String address,
        BigDecimal total_price,
        Instant date,
        String status,
        List<OrderItemResponseDto> items
){
}
