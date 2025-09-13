package org.project.orderservice.dtos.onRequest.orders;

import lombok.Builder;
import org.project.orderservice.dtos.onRequest.products.UpdateProductDto;

import java.util.List;

@Builder
public record UpdateOrderDto(
        String address,
        List<String> onDelete,
        List<UpdateOrderItemDto> updateItems
){
}
