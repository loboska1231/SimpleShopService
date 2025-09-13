package org.project.orderservice.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.orderservice.dtos.onRequest.products.CreateProductDto;
import org.project.orderservice.dtos.onRequest.products.UpdateProductDto;
import org.project.orderservice.dtos.onResponse.ProductResponseDto;
import org.project.orderservice.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() { // permit all
        return ResponseEntity.ok(productService.findProducts());
    }
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody CreateProductDto dto){
        return ResponseEntity.ok(productService.createProduct(dto));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable String id) { // permit all
        return ResponseEntity.ok(productService.findOrderById(id).get());
    }
    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable String id, @Valid @RequestBody UpdateProductDto dto) {
        return ResponseEntity.ok(productService.updateOrder(id, dto).get());
    }
    @DeleteMapping
    public ResponseEntity<ProductResponseDto> deleteProduct(@PathVariable String id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }
}
