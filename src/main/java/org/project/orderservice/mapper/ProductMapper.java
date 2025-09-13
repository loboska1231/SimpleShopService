package org.project.orderservice.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.project.orderservice.dtos.onRequest.products.CreateProductDto;
import org.project.orderservice.dtos.onRequest.products.UpdateProductDto;
import org.project.orderservice.dtos.onResponse.ProductResponseDto;
import org.project.orderservice.models.Product;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = SPRING)
public interface ProductMapper {
    Product toEntity(CreateProductDto dto);
    ProductResponseDto toResponse(Product model);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    Product updateEntity(@MappingTarget Product entity, UpdateProductDto dto);
}
