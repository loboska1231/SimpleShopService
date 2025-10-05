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
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, imports = Instant.class)
public interface OrderMapper {
//    @Mapping(target = "products", ignore = true)
    @Mapping(target = "status", constant = "CREATED")
    @Mapping(target = "date", expression = "java(Instant.now())")
    OrderEntity toEntity(CreateOrderDto dto);

    @Mapping(target = "total_price", source = "totalPrice")
    OrderResponseDto toDto(OrderEntity order);

    OrderItemEntity toItemEntity(CreateOrderItemDto dto);
    OrderItemEntity toItemEntity(UpdateOrderItemDto dto);

    OrderItemResponseDto toItemDto(OrderItemEntity orderItem);

    default OrderEntity updateOrder(@MappingTarget OrderEntity order , UpdateOrderDto dto){
        if(!ObjectUtils.isEmpty(dto)){
            order.setStatus("UPDATED");
            order.setAddress(dto.address());
            List<UpdateOrderItemDto> dtoItems = dto.updateItems();
            if(!CollectionUtils.isEmpty(dtoItems)){
                List<OrderItemEntity> items = order.getItems();
                List<String> productIds = order.getItems().stream().map(OrderItemEntity::getProductId).toList();
                items.forEach(item->{
                    dtoItems.forEach(dtoItem->{
                        if(item.getProductId().equals(dtoItem.productId()) && dtoItem.amount() >0 ) {
                            item.setAmount(dtoItem.amount());
                        }
                        if(!productIds.contains(dtoItem.productId()) ){
                            items.add(toItemEntity(dtoItem));
                        }
                    });
                });
            }
        }
        return order;
    }
}
