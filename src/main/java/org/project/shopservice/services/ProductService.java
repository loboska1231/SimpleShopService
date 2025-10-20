package org.project.shopservice.services;

import lombok.RequiredArgsConstructor;
import org.bson.types.Decimal128;
import org.project.shopservice.dtos.onRequest.products.CreateProductDto;
import org.project.shopservice.dtos.onRequest.products.UpdateProductDto;
import org.project.shopservice.dtos.onResponse.ProductResponseDto;
import org.project.shopservice.mapper.ProductMapper;
import org.project.shopservice.models.Product;
import org.project.shopservice.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final ProductMapper productMapper;

	public ProductResponseDto createProduct(CreateProductDto dto) {
		Product product = productMapper.toEntity(dto);
		Product save = productRepository.save(product);
		return productMapper.toResponse(save);
	}

	public List<ProductResponseDto> findProducts(BigDecimal min, BigDecimal max, String category) {
		List<Product> products;
		if (min != null && max != null && category != null) {
			products = productRepository.findAllByPriceGreaterThanEqualAndPriceLessThanEqualAndCategoryIsIgnoreCase(new Decimal128(min), new Decimal128(max), category);
		} else if (min != null && max != null) {

			if (max.compareTo(min) < 0) return Collections.emptyList();

			products = productRepository.findAllByPriceGreaterThanEqualAndPriceLessThanEqual(new Decimal128(min), new Decimal128(max));
		} else if (max != null && category != null) {
			products = productRepository.findAllByPriceLessThanEqualAndCategoryIsIgnoreCase(new Decimal128(max), category);
		} else if (min != null && category != null) {
			products = productRepository.findAllByPriceGreaterThanEqualAndCategoryIsIgnoreCase(new Decimal128(min), category);
		} else if (min != null) {
			products = productRepository.findAllByPriceLessThanEqual(new Decimal128(min));
		} else if (max != null) {
			products = productRepository.findAllByPriceGreaterThanEqual(new Decimal128(max));
		} else if (category != null) {
			products = productRepository.findAllByCategoryIgnoreCase(category);
		}
		else products = productRepository.findAll();
		return products.stream().map(productMapper::toResponse).toList();
	}

	public Optional<ProductResponseDto> findProductById(String id) {
		return productRepository.findById(id).map(productMapper::toResponse);
	}

	public Optional<ProductResponseDto> updateProduct(String id, UpdateProductDto dto) {
		return productRepository
				.findById(id)
				.map(product -> productMapper.updateModel(product, dto))
				.map(productRepository::save)
				.map(productMapper::toResponse);
	}

	public void deleteProductById(String id) {
		productRepository.deleteById(id);
	}
}
