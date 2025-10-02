package org.project.shopservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.shopservice.dtos.onRequest.products.CreateProductDto;
import org.project.shopservice.dtos.onRequest.products.UpdateProductDto;
import org.project.shopservice.dtos.onResponse.ProductResponseDto;
import org.project.shopservice.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

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
        return ResponseEntity.ok(productService.findProducts(min,max,category));
    }
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody CreateProductDto dto){
        return ResponseEntity.ok(productService.createProduct(dto));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable String id) { // permit all
        return ResponseEntity.ok(productService.findProductById(id).orElseThrow());
    }
    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable String id, @Valid @RequestBody UpdateProductDto dto) {
        return ResponseEntity.ok(productService.updateProduct(id, dto).orElseThrow());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ProductResponseDto> deleteProduct(@PathVariable String id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }
}
