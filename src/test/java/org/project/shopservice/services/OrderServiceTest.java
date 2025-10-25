package org.project.shopservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.shopservice.dtos.onRequest.orders.CreateOrderDto;
import org.project.shopservice.dtos.onRequest.orders.CreateOrderItemDto;
import org.project.shopservice.dtos.onRequest.orders.UpdateOrderDto;
import org.project.shopservice.dtos.onRequest.orders.UpdateOrderItemDto;
import org.project.shopservice.entities.OrderEntity;
import org.project.shopservice.entities.OrderItemEntity;
import org.project.shopservice.mapper.OrderMapper;
import org.project.shopservice.mapper.OrderMapperImpl;
import org.project.shopservice.mapper.ProductMapper;
import org.project.shopservice.mapper.ProductMapperImpl;
import org.project.shopservice.models.Product;
import org.project.shopservice.repository.OrderRepository;
import org.project.shopservice.repository.ProductRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

	@Mock
	private OrderRepository orderRepository;
	@Spy
	private OrderMapper orderMapper = new OrderMapperImpl();
	@Mock
	private UserService userService;

	@Mock
	private EmailService emailService;

	@Mock
	private ProductRepository productRepository;

	@Spy
	private ProductMapper productMapper = new ProductMapperImpl();

	@InjectMocks
	private OrderService orderService;

	private OrderEntity order1;
	private List<OrderItemEntity> orderItems1;
	private CreateOrderDto createOrderDto;
	private List<Product> products;
	@BeforeEach
	void setUp(){
		order1 = OrderEntity.builder()
				.id(1L)
				.whose("test test")
				.email("testing@test.com")
				.address("test address")
				.date(Instant.now())
				.status("CREATED")
				.build();
		orderItems1 = new ArrayList<>(List.of(
				new OrderItemEntity(1L, "0001","testType", new BigDecimal(500), 4L, order1),
				new OrderItemEntity(2L, "0002","testType", new BigDecimal(300), 6L, order1),
				new OrderItemEntity(3L, "0003","testType", new BigDecimal(100), 2L, order1)
		));
		order1.setItems(orderItems1);

		createOrderDto = new CreateOrderDto(
				"test address",
				List.of(
						new CreateOrderItemDto("0001", 4L),
						new CreateOrderItemDto("0002", 6L),
						new CreateOrderItemDto("0003", 2L)
				)
		);
		order1.setTotal();
		products = List.of(
				Product.builder().id("0001").price(new BigDecimal(500)).build(),
				Product.builder().id("0002").price(new BigDecimal(300)).build(),
				Product.builder().id("0003").price(new BigDecimal(100)).build()
		);
	}

	@Test
	void testFindOrders() {
		when(orderRepository.findAll())
				.thenReturn(List.of(order1));
		assertFalse(orderService.findOrders().isEmpty());
		assertEquals( 1,orderService.findOrders().size());
		assertEquals(orderService.findOrders(), Stream.of(order1)
				.map(orderEntity -> orderMapper.toDto(orderEntity))
				.toList()
		);
		verifyNoMoreInteractions(orderRepository);
	}

	@Test
	void findOrderById() {
		when(orderRepository.findById(anyLong()))
				.thenReturn(Optional.of(order1));
		assertNotNull(orderService.findOrderById(1L));
		verify(orderRepository, times(1)).findById(1L);
	}
	@Test
	void findOrderById_expectingNull() {
		assertTrue(orderService.findOrderById(2L).isEmpty());
		verify(orderRepository, times(1)).findById(2L);
	}

	@Test
	void testDeleteOrderById() {
		when(orderRepository.findById(anyLong()))
				.thenReturn(Optional.of(order1));
		orderService.deleteOrderById(1L);
		Mockito.verify(orderRepository, times(1)).findById(anyLong());
	}
	@Test
	void testDeleteOrderById_expectingIgnoreId() {
		when(orderRepository.findById(eq(3L)))
				.thenReturn(Optional.empty());
		orderService.deleteOrderById(3L);
		Mockito.verify(orderRepository, times(1)).findById(anyLong());
		Mockito.verifyNoMoreInteractions(orderRepository );
	}
	static Stream<Arguments> updateOrder() {
		return Stream.of(
				Arguments.of(
						UpdateOrderDto.builder()
						.address("new one")
						.onDelete(Collections.emptyList())
						.updateItems(List.of(
								new UpdateOrderItemDto("0001", 4L),
								new UpdateOrderItemDto("0002", 4L)
						))
						.build()),
				Arguments.of(
						UpdateOrderDto.builder()
						.address("new one")
						.onDelete(List.of("0003"))
						.updateItems(List.of(
								new UpdateOrderItemDto("0001", 4L),
								new UpdateOrderItemDto("0002", 4L)
						))
						.build()),
				Arguments.of(
						UpdateOrderDto.builder()
						.address("new one")
						.onDelete(Collections.emptyList())
						.updateItems(List.of(
								new UpdateOrderItemDto("0001", 4L)
						))
						.build()),
				Arguments.of(
						UpdateOrderDto.builder()
						.address("new one")
						.onDelete(Collections.emptyList())
						.updateItems(List.of(
								new UpdateOrderItemDto("0001", 0L)
						))
						.build()),
				Arguments.of(
						UpdateOrderDto.builder()
						.address("new one")
						.onDelete(List.of("0003","0001","0002"))
						.build()),
				Arguments.of(
						UpdateOrderDto.builder()
								.address("new one")
								.build())
		);
	}
	@ParameterizedTest
	@MethodSource("updateOrder")
	void testUpdateOrder(UpdateOrderDto dto) {
		when(orderRepository.findById(any()))
				.thenReturn(Optional.of(order1));

		lenient().when(productRepository.findAllByIdIn(anySet()))
				.thenReturn(products);

		when(orderRepository.save(any(OrderEntity.class)))
				.thenReturn(order1);
		assertDoesNotThrow(()->orderService.updateOrder(1L,dto));
		verify(orderRepository,times(1)).findById(any(Long.class));
	}
	@Test
	void testUpdateOrder_twice(){
		UpdateOrderDto dto = UpdateOrderDto.builder()
				.address("new one")
				.onDelete(List.of("0003", "0001", "0002"))
				.build();
		when(orderRepository.findById(any()))
				.thenReturn(Optional.of(order1));

		lenient().when(productRepository.findAllByIdIn(anySet()))
				.thenReturn(Collections.emptyList());

		when(orderRepository.save(any(OrderEntity.class)))
				.thenReturn(order1);
		orderService.updateOrder(1L,dto);
		orderService.updateOrder(1L,dto);
		verify(orderRepository,times(2)).findById(any(Long.class));
	}
	@Test
	void testUpdateOrder_nullDto() {
		var dto = UpdateOrderDto.builder().build();
		when(orderRepository.findById(any()))
				.thenReturn(Optional.of(order1));

		assertTrue(dto.isEmpty());
		assertDoesNotThrow(()->orderService.updateOrder(1L,dto));
		verifyNoMoreInteractions(orderRepository);
	}

	@Test
	void testUpdateOrder_expectingNoSuchElementException(){
		assertDoesNotThrow(()->orderService.updateOrder(3L,null));
		verify(orderRepository,times(0)).save(any(OrderEntity.class));
		assertTrue(orderService.updateOrder(3L,null).isEmpty());
	}

	@Test
	void testCreateOrder(){
		when(orderMapper.toEntity(any()))
				.thenReturn(order1);

		when(productRepository.findAllByIdIn(anySet()))
				.thenReturn(products);

		when(userService.fillFieldsEmailAndWhose(any(OrderEntity.class)))
				.thenReturn(order1);
		when(orderRepository.save(any(OrderEntity.class)))
				.thenReturn(order1);
		assertDoesNotThrow(()->orderService.createOrder(createOrderDto));
		assertNotNull(orderService.createOrder(createOrderDto));
		verify(orderRepository,times(2)).save(any(OrderEntity.class));
	}
	static Stream<Arguments> createOrder(){
		return Stream.of(
				Arguments.of(CreateOrderDto.builder().address(" ").items(List.of()).build()),
				Arguments.of(CreateOrderDto.builder().address(" ").items(List.of(CreateOrderItemDto.builder().build())).build()),
				Arguments.of(CreateOrderDto.builder().address("t").items(List.of()).build()),
				Arguments.of(CreateOrderDto.builder().address("t").items(List.of(CreateOrderItemDto.builder().build())).build())
		);
	}
	@ParameterizedTest
	@MethodSource("createOrder")
	void testCreateOrder_expectingIllegalArgumentException(CreateOrderDto nullDto){
		assertThrows(IllegalArgumentException.class,()->orderService.createOrder(nullDto));
		verifyNoInteractions(orderRepository);
	}

	@Test
	void testCreateOrder_expectingIllegalArgs(){
		List<CreateOrderItemDto> b = new ArrayList<>();
		b.add(CreateOrderItemDto
				.builder()
				.productId("0x01")
				.amount(5L)
				.build());
		var dto = CreateOrderDto
				.builder()
				.address("t")
				.items(b)
				.build();
		assertThrowsExactly(IllegalArgumentException.class,()->orderService.createOrder(dto));
		verifyNoInteractions(orderRepository);
	}
}