package org.project.orderservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.project.orderservice.dtos.onRequest.orders.CreateOrderDto;
import org.project.orderservice.dtos.onRequest.orders.UpdateOrderDto;
import org.project.orderservice.dtos.onResponse.OrderResponseDto;
import org.project.orderservice.entities.OrderEntity;

import java.time.Instant;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, imports = Instant.class)
public interface OrderMapper {
//    @Mapping(target = "products", ignore = true)
    @Mapping(target = "status", constant = "CREATED")
    @Mapping(target = "date", expression = "java(Instant.now())")
    OrderEntity toEntity(CreateOrderDto dto);

    @Mapping(target = "total_price", source = "totalPrice")
    OrderResponseDto toDto(OrderEntity order);

}
