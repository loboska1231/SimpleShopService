package org.project.shopservice.mapper;

import io.micrometer.common.util.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.project.shopservice.dtos.onRequest.products.CreateProductDto;
import org.project.shopservice.dtos.onRequest.products.UpdateProductDto;
import org.project.shopservice.dtos.onResponse.ProductResponseDto;
import org.project.shopservice.models.Product;

import java.math.BigDecimal;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ProductMapper {
    Product toEntity(CreateProductDto dto);
    ProductResponseDto toResponse(Product model);

//    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    default Product updateModel(@MappingTarget Product model, UpdateProductDto dto){
	    if ( dto == null ) {
		    return model;
	    }
	    if ( dto.category() != null && StringUtils.isNotBlank(dto.category())) {
		    model.setCategory( dto.category() );
	    }
	    if ( dto.price() != null && dto.price().compareTo(BigDecimal.ZERO) > 0 ) {
		    model.setPrice( dto.price() );
	    }
	    if ( dto.type() != null && StringUtils.isNotBlank(dto.type())) {
		    model.setType( dto.type() );
	    }

	    return model;
    }
}
