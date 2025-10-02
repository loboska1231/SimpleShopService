package org.project.shopservice.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.project.shopservice.dtos.onRequest.products.CreateProductDto;
import org.project.shopservice.dtos.onRequest.products.UpdateProductDto;
import org.project.shopservice.dtos.onResponse.ProductResponseDto;
import org.project.shopservice.models.Product;

import java.math.BigDecimal;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = SPRING)
public interface ProductMapper {
    Product toEntity(CreateProductDto dto);
    ProductResponseDto toResponse(Product model);

//    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    default Product updateEntity(@MappingTarget Product entity, UpdateProductDto dto){
	    if ( dto == null ) {
		    return entity;
	    }

	    if ( dto.category() != null && !dto.category().isEmpty()) {
		    entity.setCategory( dto.category() );
	    }
	    if ( dto.price() != null && dto.price().compareTo(BigDecimal.ZERO) > 0 ) {
		    entity.setPrice( dto.price() );
	    }
	    if ( dto.type() != null && !dto.type().isEmpty()) {
		    entity.setType( dto.type() );
	    }

	    return entity;
    }
}
