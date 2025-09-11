package org.project.orderservice.dtos.onRequest.orders;

import lombok.Builder;

import java.util.List;

@Builder
public record UpdateOrderDto(
        String address,
        List<String> onDelete
){
}
