package org.project.orderservice.services;

import lombok.RequiredArgsConstructor;
import org.project.orderservice.dtos.onRequest.products.CreateProductDto;
import org.project.orderservice.dtos.onRequest.products.UpdateProductDto;
import org.project.orderservice.dtos.onResponse.ProductResponseDto;
import org.project.orderservice.models.Product;
import org.project.orderservice.mapper.ProductMapper;
import org.project.orderservice.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductResponseDto createProduct(CreateProductDto dto){
        Product product = productMapper.toEntity(dto);
        Product save = productRepository.save(product);
        return productMapper.toResponse(save);
    }

    public List<ProductResponseDto> findProducts() {
        return productRepository.findAll().stream().map(productMapper::toResponse).toList();
    }

    public Optional<ProductResponseDto> findOrderById(String id) {
        return productRepository.findById(id).map(productMapper::toResponse);
    }

    public Optional<ProductResponseDto> updateOrder(String id, UpdateProductDto dto) {
        return productRepository
                .findById(id)
                .map(product -> productMapper.updateEntity(product, dto))
                .map(productMapper::toResponse);
    }

    public void deleteProductById(String id) {
        productRepository.deleteById(id);

    }
}
