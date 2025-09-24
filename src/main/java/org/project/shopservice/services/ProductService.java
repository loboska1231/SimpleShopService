package org.project.shopservice.services;

import lombok.RequiredArgsConstructor;
import org.project.shopservice.dtos.onRequest.products.CreateProductDto;
import org.project.shopservice.dtos.onRequest.products.UpdateProductDto;
import org.project.shopservice.dtos.onResponse.ProductResponseDto;
import org.project.shopservice.mapper.ProductMapper;
import org.project.shopservice.models.Product;
import org.project.shopservice.repository.ProductRepository;
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

    public Optional<ProductResponseDto> findProductById(String id) {
        return productRepository.findById(id).map(productMapper::toResponse);
    }

    public Optional<ProductResponseDto> updateProduct(String id, UpdateProductDto dto) {
        return productRepository
                .findById(id)
                .map(product -> productMapper.updateEntity(product, dto))
                .map(productMapper::toResponse);
    }

    public void deleteProductById(String id) {
        productRepository.deleteById(id);

    }
}
