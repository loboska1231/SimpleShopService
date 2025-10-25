package org.project.shopservice.controller;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.project.shopservice.dtos.onRequest.products.CreateProductDto;
import org.project.shopservice.dtos.onRequest.products.UpdateProductDto;
import org.project.shopservice.dtos.onResponse.ProductResponseDto;
import org.project.shopservice.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
	private final ProductService productService;

	@GetMapping
	public ResponseEntity<List<ProductResponseDto>> getAllProducts(
			@RequestParam(required = false) BigDecimal min,
			@RequestParam(required = false) BigDecimal max,
			@RequestParam(required = false) String category
	) { // permit all
		return ResponseEntity.ok(productService.findProducts(min, max, category));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductResponseDto> getProduct(@PathVariable String id) { // permit all
		return ResponseEntity.ok(productService.findProductById(id).orElseThrow(() -> new NoSuchElementException("Product not found")));
	}

	@RolesAllowed("ADMIN")
	@PostMapping
	public ResponseEntity<ProductResponseDto> createProduct(@Validated @RequestBody CreateProductDto dto) {
		return ResponseEntity.ok(productService.createProduct(dto));
	}

	@RolesAllowed("ADMIN")
	@PatchMapping("/{id}")
	public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable String id, @Validated @RequestBody UpdateProductDto dto) {
		return ResponseEntity.ok(productService.updateProduct(id, dto).orElseThrow(() -> new NoSuchElementException("Product not found")));
	}

	@RolesAllowed("ADMIN")
	@DeleteMapping("/{id}")
	public ResponseEntity<ProductResponseDto> deleteProduct(@PathVariable String id) {
		productService.deleteProductById(id);
		return ResponseEntity.noContent().build();
	}
}
