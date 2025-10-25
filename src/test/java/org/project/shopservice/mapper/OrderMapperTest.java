package org.project.shopservice.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.project.shopservice.dtos.onRequest.orders.CreateOrderDto;
import org.project.shopservice.dtos.onRequest.orders.CreateOrderItemDto;
import org.project.shopservice.dtos.onRequest.orders.UpdateOrderDto;
import org.project.shopservice.dtos.onRequest.orders.UpdateOrderItemDto;
import org.project.shopservice.dtos.onResponse.OrderItemResponseDto;
import org.project.shopservice.dtos.onResponse.OrderResponseDto;
import org.project.shopservice.entities.OrderEntity;
import org.project.shopservice.entities.OrderItemEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

	private final OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);
	private OrderEntity order;

	@BeforeEach
	void setUp() {
		order =  OrderEntity.builder()
				.id(1L)
				.email("test@t.com")
				.date(Instant.now())
				.status("CREATED")
				.whose("test")
				.items(new ArrayList<>(List.of(
						OrderItemEntity.builder()
								.id(1L)
								.amount(2L)
								.productId("0x1")
								.price(BigDecimal.valueOf(100))
								.build(),
						OrderItemEntity.builder()
								.id(2L)
								.amount(1L)
								.productId("0x2")
								.price(BigDecimal.valueOf(300))
								.build(),
						OrderItemEntity.builder()
								.id(3L)
								.amount(1L)
								.productId("0x3")
								.price(BigDecimal.valueOf(400))
								.build()))
				)
				.build();
		order.setTotal();
	}

	@Test
	void testToItemEntity_createOrderItemDto(){
		CreateOrderItemDto dto = CreateOrderItemDto.builder()
				.amount(4L)
				.productId("0x1")
				.build();
		OrderItemEntity result = orderMapper.toItemEntity(dto);
		assertNotNull(result);
		assertEquals(4,result.getAmount());
		assertEquals("0x1",result.getProductId());
		assertNull(result.getId());
	}
	@Test
	void testToItemEntity_createOrderItemDto_expectingNull(){
		assertNull(orderMapper.toItemEntity((CreateOrderItemDto) null));
	}
	@Test
	void testToItemEntity_UpdateOrderItemDto(){
		CreateOrderItemDto dto = CreateOrderItemDto.builder()
				.amount(4L)
				.productId("0x1")
				.build();
		OrderItemEntity result = orderMapper.toItemEntity(dto);
		assertNotNull(result);
		assertEquals(4,result.getAmount());
		assertEquals("0x1",result.getProductId());
		assertNull(result.getId());
	}

	@Test
	void testToItemDto(){
		OrderItemEntity item = OrderItemEntity.builder()
				.price(BigDecimal.valueOf(100))
				.productId("0x1")
				.amount(12L)
				.build();
		OrderItemResponseDto result = orderMapper.toItemDto(item);
		assertNotNull(result);
		assertEquals("0x1",result.productId());
		assertEquals(12,result.amount());
	}

	@Test
	void testToEntity(){
		CreateOrderDto dto = CreateOrderDto.builder()
				.address("testing")
				.items(List.of(
						CreateOrderItemDto.builder()
								.amount(4L)
								.productId("0x1")
								.build(),
						CreateOrderItemDto.builder()
								.amount(3L)
								.productId("0x2")
								.build(),
						CreateOrderItemDto.builder()
								.amount(5L)
								.productId("0x3")
								.build()
				))
				.build();
		OrderEntity result = orderMapper.toEntity(dto);

		assertNotNull(result);
		assertNotNull(result.getItems());
		assertEquals(3, result.getItems().size());
		assertEquals("testing", result.getAddress());
		assertEquals("CREATED", result.getStatus());
		assertArrayEquals(
				result.getItems().stream().map(OrderItemEntity::getProductId).toArray(),
				dto.items().stream().map(CreateOrderItemDto::productId).toArray()
		);
	}
	@Test
	void testToDto(){
		OrderResponseDto result = orderMapper.toDto(order);
		assertNotNull(result);
		assertEquals(1, result.id());
		assertEquals("test",result.whose());
		assertEquals(3,result.items().size());
		assertEquals("CREATED",result.status());
		assertEquals(BigDecimal.valueOf(900),result.total_price());
		assertArrayEquals(
				order.getItems().stream().map(OrderItemEntity::getProductId).toArray(),
				result.items().stream().map(OrderItemResponseDto::productId).toArray()
		);
	}

	@Test
	void testUpdateOrder(){
		UpdateOrderDto dto = UpdateOrderDto.builder()
				.address("test 2")
				.onDelete(Collections.emptyList())
				.updateItems(new ArrayList<>(List.of(
						UpdateOrderItemDto.builder()
								.productId("0x1")
								.amount(6L)
								.build(),
						UpdateOrderItemDto.builder()
								.productId("0x4")
								.amount(1L)
								.build()
				)))
				.build();
		order = orderMapper.updateOrder(order,dto);
		assertNotNull(order);
		assertNotNull(order.getItems());
		assertEquals(4, order.getItems().size());
		assertEquals("UPDATED",order.getStatus());
		assertArrayEquals(
				List.of("0x1","0x2","0x3","0x4").toArray(),
				order.getItems().stream().map(OrderItemEntity::getProductId).toArray()
		);
		assertEquals(6,order.getItems().stream().filter(item->item.getProductId().equals("0x1")).findFirst().get().getAmount());
	}
	@Test
	void testUpdateOrder_changedAddressAndStatus(){
		UpdateOrderDto dto = UpdateOrderDto.builder()
				.address("test 2")
				.onDelete(Collections.emptyList())
				.updateItems(List.of())
				.build();
		order = orderMapper.updateOrder(order,dto);
		assertNotNull(order);
		assertNotNull(order.getItems());
		assertEquals(3, order.getItems().size());
		assertEquals("UPDATED",order.getStatus());
		assertArrayEquals(
				List.of("0x1","0x2","0x3").toArray(),
				order.getItems().stream().map(OrderItemEntity::getProductId).toArray()
		);
	}

}