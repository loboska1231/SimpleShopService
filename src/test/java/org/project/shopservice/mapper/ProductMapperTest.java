package org.project.shopservice.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.project.shopservice.dtos.onRequest.products.CreateProductDto;
import org.project.shopservice.dtos.onRequest.products.UpdateProductDto;
import org.project.shopservice.dtos.onResponse.ProductResponseDto;
import org.project.shopservice.models.Product;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductMapperTest {

	private final ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

	private Product product;

	@BeforeEach
	void setProduct(){
		product = Product.builder()
				.id("0x1")
				.category("test")
				.type("t")
				.price(BigDecimal.valueOf(600))
				.build();
	}

	@Test
	void testToEntity() {
		CreateProductDto dto = CreateProductDto.builder()
				.category("test")
				.type("t")
				.price(BigDecimal.valueOf(600))
				.build();
		Product entity = productMapper.toEntity(dto);
		assertNotNull(entity);
		assertEquals("test", entity.getCategory());
		assertEquals("t", entity.getType());
		assertEquals(BigDecimal.valueOf(600), entity.getPrice());
	}
	@Test
	void testToEntity_expectingNull() {
		assertNull(productMapper.toEntity(null));
	}
	@Test
	void testToEntity_DoesNotThrowException_EmptyDto() {
		assertDoesNotThrow(()->productMapper.toEntity(CreateProductDto.builder().build()));
		// @NonNull from lombok
		// @NotBlank is replacing @NotNull, both works on the controller layer, both from jakarta.validation
	}

	@Test
	void testToResponse() {
		ProductResponseDto responseDto = productMapper.toResponse(product);
		assertNotNull(responseDto);
		assertEquals("0x1", responseDto.id());
		assertEquals("test", responseDto.category());
		assertEquals("t", responseDto.type());
		assertEquals(BigDecimal.valueOf(600), responseDto.price());
	}
	@Test
	void testToResponse_expectingNull(){
		assertNull(productMapper.toResponse(null));
	}

	@Test
	void testUpdateModel(){
		UpdateProductDto dto = UpdateProductDto.builder()
				.category("testing")
				.type("test")
				.price(BigDecimal.valueOf(100))
				.build();
		Product result = productMapper.updateModel(product,dto);
		assertNotNull(result);
		assertEquals("testing", result.getCategory());
		assertEquals("test", result.getType());
		assertEquals(BigDecimal.valueOf(100), result.getPrice());
	}
	@Test
	void testUpdateModel_expectingUnchangedProduct(){
		UpdateProductDto dto = UpdateProductDto.builder()
				.build();
		Product result = productMapper.updateModel(product,dto);
		assertNotNull(result);
		assertEquals("test", result.getCategory());
		assertEquals("t", result.getType());
		assertEquals(BigDecimal.valueOf(600), result.getPrice());
	}
	@Test
	void testUpdateModel_expectingUnchangedCategory(){
		UpdateProductDto dto = UpdateProductDto.builder()
				.type("test")
				.price(BigDecimal.valueOf(100))
				.build();
		Product result = productMapper.updateModel(product,dto);
		assertNotNull(result);
		assertEquals("test", result.getCategory());
		assertEquals("test", result.getType());
		assertEquals(BigDecimal.valueOf(100), result.getPrice());
	}
	@Test
	void testUpdateModel_expectingUnchangedCategoryAndType(){
		UpdateProductDto dto = UpdateProductDto.builder()
				.price(BigDecimal.valueOf(100))
				.build();
		Product result = productMapper.updateModel(product,dto);
		assertNotNull(result);
		assertEquals("test", result.getCategory());
		assertEquals("t", result.getType());
		assertEquals(BigDecimal.valueOf(100), result.getPrice());
	}
	@Test
	void testUpdateModel_expectingUnchangedPrice(){
		UpdateProductDto dto = UpdateProductDto.builder()
				.type("test")
				.category("testing")
				.build();
		Product result = productMapper.updateModel(product,dto);
		assertNotNull(result);
		assertEquals("testing", result.getCategory());
		assertEquals("test", result.getType());
		assertEquals(BigDecimal.valueOf(600), result.getPrice());
	}
	@Test
	void testUpdateModel_expectingUnchangedProduct_checkConditions(){
		UpdateProductDto dto = UpdateProductDto.builder()
				.type("  ")
				.category("     ")
				.price(BigDecimal.valueOf(0))
				.build();
		Product result = productMapper.updateModel(product,dto);
		assertNotNull(result);
		assertEquals("test", result.getCategory());
		assertEquals("t", result.getType());
		assertEquals(BigDecimal.valueOf(600), result.getPrice());
	}
	@Test
	void testUpdateModel_expectingUnchangedCategory_checkConditions(){
		UpdateProductDto dto = UpdateProductDto.builder()
				.type("test")
				.category("")
				.price(BigDecimal.valueOf(-1))
				.build();
		Product result = productMapper.updateModel(product,dto);
		assertNotNull(result);
		assertEquals("test", result.getCategory());
		assertEquals("test", result.getType());
		assertEquals(BigDecimal.valueOf(600), result.getPrice());
	}
}