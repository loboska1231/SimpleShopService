package org.project.shopservice.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.project.shopservice.dtos.onRequest.products.CreateProductDto;
import org.project.shopservice.dtos.onRequest.products.UpdateProductDto;
import org.project.shopservice.dtos.onResponse.ProductResponseDto;
import org.project.shopservice.models.Product;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = SPRING)
public interface ProductMapper {
    Product toEntity(CreateProductDto dto);
    ProductResponseDto toResponse(Product model);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    Product updateEntity(@MappingTarget Product entity, UpdateProductDto dto);
}
