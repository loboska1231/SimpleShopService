package org.project.shopservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.shopservice.dtos.onRequest.orders.CreateOrderDto;
import org.project.shopservice.dtos.onRequest.orders.UpdateOrderDto;
import org.project.shopservice.dtos.onResponse.ErrorDto;
import org.project.shopservice.dtos.onResponse.OrderResponseDto;
import org.project.shopservice.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Orders URI's that you can use to sending requests")
public class OrderController {

	private final OrderService orderService;

	@Operation(
			operationId = "getOrders",
			summary = "Get orders",
			description = "Get all orders if you are authenticated and have role ADMIN ",
			security = @SecurityRequirement(name = "BearerSecurityScheme")
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "OK",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderResponseDto.class)))),
			@ApiResponse(
					responseCode = "403",
					description = "FORBIDDEN",
					content = @Content(schema = @Schema(implementation = ErrorDto.class))
			)
	})
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping
	public ResponseEntity<List<OrderResponseDto>> getOrders() {
		return ResponseEntity.ok(orderService.findOrders());
	}

	@Operation(
			operationId = "createOrder",
			summary = "Create order",
			description = "Create order if you are authenticated",
			security = @SecurityRequirement(name = "BearerSecurityScheme"),
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true)
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "OK",
					content = @Content(schema =@Schema(implementation = OrderResponseDto.class))),
			@ApiResponse(
					responseCode = "400",
					description = "BAD_REQUEST",
					content =  @Content(schema = @Schema(implementation = ErrorDto.class))),
			@ApiResponse(
					responseCode = "403",
					description = "FORBIDDEN",
					content = @Content(schema = @Schema(implementation = ErrorDto.class))
			)
	})
	@PostMapping
	public ResponseEntity<OrderResponseDto> createOrder(@Validated @RequestBody CreateOrderDto dto) {
		return ResponseEntity.ok(orderService.createOrder(dto));
	}

	@Operation(
			operationId = "getOrder",
			summary = "Get order",
			description = "Get order if you are authenticated and this order is yours",
			security = @SecurityRequirement(name = "BearerSecurityScheme"),
			parameters = @Parameter(name = "id", required = true, in = ParameterIn.PATH, schema = @Schema(type = "integer"))
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "OK",
					content = @Content(schema = @Schema(implementation = OrderResponseDto.class))),
			@ApiResponse(
					responseCode = "404",
					description = "NOT_FOUND",
					content =  @Content(schema = @Schema(implementation = ErrorDto.class))),
			@ApiResponse(
					responseCode = "403",
					description = "FORBIDDEN",
					content = @Content(schema = @Schema(implementation = ErrorDto.class))
			)
	})
	@GetMapping("/{id}")
	@PostAuthorize("hasRole('ADMIN') or returnObject.body.email() == authentication.name")
	public ResponseEntity<OrderResponseDto> getOrder(@PathVariable Long id) {
		return ResponseEntity.ok(orderService.findOrderById(id).orElseThrow(() -> new NoSuchElementException("Order not found")));
	}

	@Operation(
			operationId = "updateOrder",
			summary = "Update order",
			description = "Update order, PATCH if you are authenticated and this order is yours",
			security = @SecurityRequirement(name = "BearerSecurityScheme"),
			parameters = @Parameter(name = "id", required = true, in = ParameterIn.PATH, schema = @Schema(type = "integer")),
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content =  @Content(schema = @Schema(implementation = UpdateOrderDto.class)))
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "OK",
					content = @Content(schema = @Schema(implementation = OrderResponseDto.class))),
			@ApiResponse(
					responseCode = "404",
					description = "NOT_FOUND",
					content =  @Content(schema = @Schema(implementation = ErrorDto.class))),
			@ApiResponse(
					responseCode = "403",
					description = "FORBIDDEN",
					content = @Content(schema = @Schema(implementation = ErrorDto.class))
			)
	})
	@PatchMapping("/{id}")
	@PostAuthorize("hasRole('ADMIN') or returnObject.body.email() == authentication.name")
	public ResponseEntity<OrderResponseDto> updateOrder(@PathVariable Long id, @Validated @RequestBody UpdateOrderDto dto) {
		return ResponseEntity.ok(orderService.updateOrder(id, dto).orElseThrow((() -> new NoSuchElementException("Order not found"))));
	}

	@Operation(
			operationId = "deleteOrder",
			summary = "Delete order",
			description = "Delete order if you are authenticated and this order is yours",
			security = @SecurityRequirement(name = "BearerSecurityScheme"),
			parameters = @Parameter(name = "id", required = true, in = ParameterIn.PATH, schema = @Schema(type = "integer"))
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "204",
					description = "NO_CONTENT",
					content = @Content(schema = @Schema())),
			@ApiResponse(
					responseCode = "403",
					description = "FORBIDDEN",
					content = @Content(schema = @Schema(implementation = ErrorDto.class))
			)
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<OrderResponseDto> deleteOrder(@PathVariable Long id) {
		orderService.deleteOrderById(id);
		return ResponseEntity.noContent().build();
	}



}
