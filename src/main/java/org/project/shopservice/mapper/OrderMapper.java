package org.project.shopservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.project.shopservice.dtos.onRequest.orders.CreateOrderDto;
import org.project.shopservice.dtos.onRequest.orders.CreateOrderItemDto;
import org.project.shopservice.dtos.onRequest.orders.UpdateOrderDto;
import org.project.shopservice.dtos.onRequest.orders.UpdateOrderItemDto;
import org.project.shopservice.dtos.onResponse.OrderItemResponseDto;
import org.project.shopservice.dtos.onResponse.OrderResponseDto;
import org.project.shopservice.entities.OrderEntity;
import org.project.shopservice.entities.OrderItemEntity;

import java.time.Instant;
import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, imports = Instant.class)
public interface OrderMapper {
//    @Mapping(target = "products", ignore = true)
    @Mapping(target = "status", constant = "CREATED")
    @Mapping(target = "date", expression = "java(Instant.now())")
    OrderEntity toEntity(CreateOrderDto dto);

    @Mapping(target = "total_price", source = "totalPrice")
    OrderResponseDto toDto(OrderEntity order);

    @Mapping(target ="items", source = "updateItems")
    @Mapping(target = "status", constant = "UPDATED")
    OrderEntity updateOrder(@MappingTarget OrderEntity order,UpdateOrderDto dto);

    OrderItemEntity toItemEntity(CreateOrderItemDto dto);
    OrderItemEntity toItemEntity(UpdateOrderItemDto dto);

    OrderItemResponseDto toItemDto(OrderItemEntity orderItem);


    default OrderEntity updateOrderItems(@MappingTarget OrderEntity order ,UpdateOrderDto dto){
        order.setStatus("UPDATED");
        List<OrderItemEntity> items = order.getItems();
        List<UpdateOrderItemDto> dtoItems = dto.updateItems();
        items.forEach(item->{
            dtoItems.forEach(dtoItem->{
                if(item.getProductId().equals(dtoItem.productId())){
                    item.setAmount(dtoItem.amount());
                } else{
                    items.add(toItemEntity(dtoItem));
                }
            });
        });
        return order;
    }
}
