package org.project.shopservice.mapper;

import org.mapstruct.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

	default OrderEntity updateOrder(@MappingTarget OrderEntity order, UpdateOrderDto dto) {
		if (dto != null) {
			order.setStatus("UPDATED");
			if(dto.address()!=null){
				order.setAddress(dto.address());
			}
			List<UpdateOrderItemDto> dtoItems = dto.updateItems();
			if (!CollectionUtils.isEmpty(dtoItems)) {
				List<OrderItemEntity> items = order.getItems();

				Map<String, OrderItemEntity> itemsMap = items.stream()
						.collect(Collectors.toMap(OrderItemEntity::getProductId, item -> item)); // creating map to avoid second forEach, + .equals. toMap creates HashMap

				dtoItems.forEach(dtoItem -> {
					if (dtoItem.amount() <= 0) return; // skip item with amount <0
					OrderItemEntity item = itemsMap.get(dtoItem.productId());
					if (item != null) item.setAmount(dtoItem.amount()); // that actually works, as pointers in C
                    else {
						OrderItemEntity newI = toItemEntity(dtoItem);
						items.add(newI); // spring manages it w/o our help
						itemsMap.put(newI.getProductId(), newI);
						// adding item in itemsMap to avoid copies in items
					}
				});
			}
		}
		return order;
	}
}
