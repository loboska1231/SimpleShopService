package org.project.orderservice.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.project.orderservice.dtos.onRequest.products.CreateProductDto;
import org.project.orderservice.dtos.onRequest.products.UpdateProductDto;
import org.project.orderservice.dtos.onResponse.ProductResponseDto;
import org.project.orderservice.entities.ProductEntity;
import org.project.orderservice.repository.ProductRepository;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = SPRING)
public interface ProductMapper {
    ProductEntity toEntity(CreateProductDto dto);
    ProductResponseDto toResponse(ProductEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    ProductEntity updateEntity(@MappingTarget ProductEntity entity, UpdateProductDto dto);
}
