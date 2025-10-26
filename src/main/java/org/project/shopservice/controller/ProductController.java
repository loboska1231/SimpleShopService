package org.project.shopservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.shopservice.dtos.onRequest.products.CreateProductDto;
import org.project.shopservice.dtos.onRequest.products.UpdateProductDto;
import org.project.shopservice.dtos.onResponse.ErrorDto;
import org.project.shopservice.dtos.onResponse.ProductResponseDto;
import org.project.shopservice.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
@Tag(name = "Products", description = "Products URI's that you can use to sending requests")
public class ProductController {
	private final ProductService productService;

	@Operation(
			operationId = "getProducts",
			summary = "Get Products",
			description = "Get all Products w/ parameters or w/o them",
			parameters = {
					@Parameter(name = "min", description = "minimum price", schema = @Schema(type = "number")),
					@Parameter(name = "max", description = "maximum price", schema = @Schema(type = "number")),
					@Parameter(name = "category", description = "search by category", schema = @Schema(type = "string")),
			}
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "OK",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductResponseDto.class)))),
			@ApiResponse(
					responseCode = "403",
					description = "FORBIDDEN",
					content = @Content(schema = @Schema(implementation = ErrorDto.class))
			)
	})

	@GetMapping
	public ResponseEntity<List<ProductResponseDto>> getAllProducts(
			@RequestParam(required = false) BigDecimal min,
			@RequestParam(required = false) BigDecimal max,
			@RequestParam(required = false) String category
	) { // permit all
		return ResponseEntity.ok(productService.findProducts(min, max, category));
	}

	@Operation(
			operationId = "getProduct",
			summary = "Get Product",
			description = "Get Product",
			parameters = {
					@Parameter(name = "id", description = "productId", schema = @Schema(type = "string"))
			}
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "OK",
					content = @Content(schema = @Schema(implementation = ProductResponseDto.class))),
			@ApiResponse(
					responseCode = "400",
					description = "NOT_FOUND",
					content = @Content(schema = @Schema(implementation = ErrorDto.class))
			),
			@ApiResponse(
					responseCode = "403",
					description = "FORBIDDEN",
					content = @Content(schema = @Schema(implementation = ErrorDto.class))
			)
	})
	@GetMapping("/{id}")
	public ResponseEntity<ProductResponseDto> getProduct(@PathVariable String id) { // permit all
		return ResponseEntity.ok(productService.findProductById(id).orElseThrow(() -> new NoSuchElementException("Product not found")));
	}

	@Operation(
			operationId = "createProduct",
			summary = "Create Product",
			description = "Create Product if you are authenticated and have role ADMIN",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					required = true,
					content = @Content(schema = @Schema(implementation = CreateProductDto.class))),
			security = {
					@SecurityRequirement(name = "BearerSecurityScheme")
			})
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "OK",
					content = @Content(schema = @Schema(implementation = ProductResponseDto.class))),
			@ApiResponse(
					responseCode = "404",
					description = "BAD_REQUEST",
					content = @Content(schema = @Schema(implementation = ErrorDto.class))
			),
			@ApiResponse(
					responseCode = "403",
					description = "FORBIDDEN",
					content = @Content(schema = @Schema(implementation = ErrorDto.class))
			)
	})
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<ProductResponseDto> createProduct(@Validated @RequestBody CreateProductDto dto) {
		return ResponseEntity.ok(productService.createProduct(dto));
	}

	@Operation(
			operationId = "updateProduct",
			summary = "Update Product",
			description = "Update Product if you are authenticated and have role ADMIN",
			parameters = {
					@Parameter(name = "id", description = "product Id", schema = @Schema(type = "string"))
			},
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					required = true,
					content = @Content(schema = @Schema(implementation = UpdateProductDto.class))),
			security = {
					@SecurityRequirement(name = "BearerSecurityScheme")
			})
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "OK",
					content = @Content(schema = @Schema(implementation = ProductResponseDto.class))),
			@ApiResponse(
					responseCode = "404",
					description = "BAD_REQUEST",
					content = @Content(schema = @Schema(implementation = ErrorDto.class))
			),
			@ApiResponse(
					responseCode = "400",
					description = "NOT_FOUND",
					content = @Content(schema = @Schema(implementation = ErrorDto.class))
			),
			@ApiResponse(
					responseCode = "403",
					description = "FORBIDDEN",
					content = @Content(schema = @Schema(implementation = ErrorDto.class))
			)
	})
	@PreAuthorize("hasRole('ADMIN')")
	@PatchMapping("/{id}")
	public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable String id, @Validated @RequestBody UpdateProductDto dto) {
		return ResponseEntity.ok(productService.updateProduct(id, dto).orElseThrow(() -> new NoSuchElementException("Product not found")));
	}

	@Operation(
			operationId = "deleteProduct",
			summary = "Delete Product",
			description = "Delete Product if you are authenticated and have role ADMIN",
			parameters = {
					@Parameter(name = "id", description = "product Id", schema = @Schema(type = "string"))
			},
			security = {
					@SecurityRequirement(name = "BearerSecurityScheme")
			})
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
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<ProductResponseDto> deleteProduct(@PathVariable String id) {
		productService.deleteProductById(id);
		return ResponseEntity.noContent().build();
	}
}
