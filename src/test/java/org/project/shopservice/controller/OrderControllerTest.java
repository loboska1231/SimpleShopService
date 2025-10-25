package org.project.shopservice.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.shopservice.dtos.onRequest.orders.CreateOrderDto;
import org.project.shopservice.dtos.onRequest.orders.UpdateOrderDto;
import org.project.shopservice.dtos.onResponse.OrderItemResponseDto;
import org.project.shopservice.dtos.onResponse.OrderResponseDto;
import org.project.shopservice.models.Product;
import org.project.shopservice.security.config.SecurityConfig;
import org.project.shopservice.security.filters.JwtFilter;
import org.project.shopservice.security.utils.JwtUtil;
import org.project.shopservice.services.AuthService;
import org.project.shopservice.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@ExtendWith(MockitoExtension.class)
@Import({SecurityConfig.class, JwtFilter.class})
@WithMockUser
class OrderControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockitoBean
	private OrderService orderService;

	@MockitoBean
	private JwtUtil jwtUtil;
	@MockitoBean
	private AuthService authService;
	@MockitoBean
	private PasswordEncoder passwordEncoder;

	private Instant now;
	private Product product;
	private OrderResponseDto dto;
	private List<OrderResponseDto> orders;

	@BeforeEach
	void init() {
		now = Instant.now();
		product = Product.builder()
				.id("0x1")
				.type("test")
				.category("testing")
				.price(BigDecimal.valueOf(100))
				.build();
		dto = OrderResponseDto.builder()
				.id(1L)
				.whose("tester test")
				.email("test@test.com")
				.address("test address")
				.date(now)
				.total_price(BigDecimal.valueOf(600))
				.status("CREATED")
				.items(List.of(OrderItemResponseDto.builder()
						.amount(6L)
						.productId("0x1")
						.build()))
				.build();
		orders = List.of(
				OrderResponseDto.builder().build(),
				OrderResponseDto.builder().build(),
				dto
		);
	}


	@Test
	@SneakyThrows
	void testCreateOrder_200ExpectingIsOk_NotNullBody() {
		when(orderService.createOrder(any(CreateOrderDto.class)))
				.thenReturn(dto);
		mockMvc.perform(
						post("/orders")
								.contentType(MediaType.APPLICATION_JSON)
								.content("""
										{
											"address": "test address",
											"items": [
												{
													"productId": "0x1",
													"amount": 6
												}
											]
										}
										""")
				).andExpect(status().isOk())
				.andExpect(jsonPath("$").exists())
				.andExpect(jsonPath("$.id").value("1"))
				.andExpect(jsonPath("$.whose").value("tester test"))
				.andExpect(jsonPath("$.email").value("test@test.com"))
				.andExpect(jsonPath("$.address").value("test address"))
				.andExpect(jsonPath("$.total_price").value(600))
				.andExpect(jsonPath("$.status").value("CREATED"))
				.andExpect(jsonPath("$.items[0].amount").value(6))
				.andExpect(jsonPath("$.items[0].productId").value("0x1"));
	}
	static Stream<Arguments> testArgsForCreateOrder404(){
		return Stream.of(
				Arguments.of("","",0),
				Arguments.of("  ","",0),
				Arguments.of("testtesttesttesttesttesttesttest","",0),
				Arguments.of("test","  ",0),
				Arguments.of("test","0x1",0),
				Arguments.of("test","0x1",-1)
		);
	}
	@ParameterizedTest
	@MethodSource("testArgsForCreateOrder404")
	@SneakyThrows
	void testCreateOrder_404ExpectingBadRequest(String address, String id, int amount) {
		mockMvc.perform(
				post("/orders")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
									"address": "%s",
									"items": [
										{
											"productId": "%s",
											"amount": %d
										}
									]
								}
								""".formatted(address,id,amount))
		).andExpect(status().isBadRequest());
		verifyNoInteractions(orderService);
	}
	@Test
	@SneakyThrows
	@WithMockUser(roles = "ADMIN")
	void testGetOrders_200ExpectingIsOk_NotNullBody() {
		when(orderService.findOrders())
				.thenReturn(orders);
		mockMvc.perform(get("/orders"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").exists())
				.andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$[2].id").value("1"))
				.andExpect(jsonPath("$[2].whose").value("tester test"))
				.andExpect(jsonPath("$[2].email").value("test@test.com"))
				.andExpect(jsonPath("$[2].address").value("test address"))
				.andExpect(jsonPath("$[2].total_price").value(600))
				.andExpect(jsonPath("$[2].status").value("CREATED"))
				.andExpect(jsonPath("$[2].items[0].amount").value(6))
				.andExpect(jsonPath("$[2].items[0].productId").value("0x1"));
		verify(orderService).findOrders();
	}

	@Test
	@SneakyThrows
	void testGetOrder_200ExpectingIsOk_NotNullBody() {
		when(orderService.findOrderById(eq(1L)))
				.thenReturn(Optional.of(dto));
		mockMvc.perform(get("/orders/{id}", 1))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").exists())
				.andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$.id").value("1"))
				.andExpect(jsonPath("$.whose").value("tester test"))
				.andExpect(jsonPath("$.email").value("test@test.com"))
				.andExpect(jsonPath("$.address").value("test address"))
				.andExpect(jsonPath("$.total_price").value(600))
				.andExpect(jsonPath("$.status").value("CREATED"))
				.andExpect(jsonPath("$.items[0].amount").value(6))
				.andExpect(jsonPath("$.items[0].productId").value("0x1"));
	}
	@Test
	@SneakyThrows
	void testGetOrder_200ExpectingIsOk_NullBody() {
		when(orderService.findOrderById(eq(2L)))
				.thenReturn(Optional.of(dto));
		mockMvc.perform(get("/orders/{id}", 2))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").exists());
		verify(orderService).findOrderById(eq(2L));
		assertNotNull(orderService.findOrderById(2L));
	}

	@Test
	@SneakyThrows
	void testUpdateOrder_200ExpectingIsOk_NotNullBody() {
		UpdateOrderDto updateOrderDto = UpdateOrderDto.builder()
				.address("test new")
				.build();
		when(orderService.updateOrder(eq(1L), any(UpdateOrderDto.class)))
				.thenReturn(Optional.of(dto.toBuilder().status("UPDATED").address(updateOrderDto.address()).build()));
		mockMvc.perform(patch("/orders/{id}", 1)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
									"address": "test new"
								}
								""")
				).andExpect(status().isOk())
				.andExpect(jsonPath("$").exists())
				.andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$.id").value("1"))
				.andExpect(jsonPath("$.whose").value("tester test"))
				.andExpect(jsonPath("$.email").value("test@test.com"))
				.andExpect(jsonPath("$.address").value("test new"))
				.andExpect(jsonPath("$.total_price").value(600))
				.andExpect(jsonPath("$.status").value("UPDATED"))
				.andExpect(jsonPath("$.items[0].amount").value(6))
				.andExpect(jsonPath("$.items[0].productId").value(product.getId()));
		verify(orderService).updateOrder(eq(1L), any(UpdateOrderDto.class));
	}
	@Test
	@SneakyThrows
	void testUpdateOrder_200ExpectingIsOk_NothingChanged() {
		when(orderService.updateOrder(eq(1L), any(UpdateOrderDto.class)))
				.thenReturn(Optional.of(dto));
		mockMvc.perform(patch("/orders/{id}", 1)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
									"address": "",
									"onDelete": [],
									"updateItems": []
								}
								""")
				).andExpect(status().isOk())
				.andExpect(jsonPath("$").exists())
				.andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$.id").value(dto.id()))
				.andExpect(jsonPath("$.whose").value(dto.whose()))
				.andExpect(jsonPath("$.email").value(dto.email()))
				.andExpect(jsonPath("$.address").value(dto.address()))
				.andExpect(jsonPath("$.total_price").value(dto.total_price()))
				.andExpect(jsonPath("$.status").value(dto.status()))
				.andExpect(jsonPath("$.items[0].amount").value(dto.items().get(0).amount()))
				.andExpect(jsonPath("$.items[0].productId").value(dto.items().get(0).productId()));
		verify(orderService).updateOrder(eq(1L), any(UpdateOrderDto.class));
	}

	@Test
	@SneakyThrows
	void testUpdateOrder_ExpectingNoSuchElementException(){
		mockMvc.perform(patch("/orders/{id}", 2)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
								{
									"address": "",
									"onDelete": [],
									"updateItems": []
								}
								"""))
				.andExpect(status().isNotFound());
	verify(orderService).updateOrder(any(Long.class),any(UpdateOrderDto.class));
	}

	@Test
	@SneakyThrows
	void testDeleteOrder_200ExpectingNoContent() {
		mockMvc.perform(delete("/orders/{id}", 1))
				.andExpect(status().isNoContent());
		verify(orderService).deleteOrderById(eq(1L));
	}
}