package org.project.shopservice.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.shopservice.dtos.onRequest.products.CreateProductDto;
import org.project.shopservice.dtos.onRequest.products.UpdateProductDto;
import org.project.shopservice.dtos.onResponse.ProductResponseDto;
import org.project.shopservice.mapper.ProductMapper;
import org.project.shopservice.mapper.ProductMapperImpl;
import org.project.shopservice.models.Product;
import org.project.shopservice.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

	@Mock
	ProductRepository productRepository;
	@Spy
	ProductMapper productMapper = new ProductMapperImpl();
	@InjectMocks
	ProductService productService;

	private Product product1;
	private Product product2;
	private Product product3;

	@BeforeEach
	void setUp() {
		product1 = Product.builder()
				.id("00001")
				.category("testing")
				.price(BigDecimal.valueOf(110))
				.type("test")
				.build();
		product2 = Product.builder()
				.id("00002")
				.category("testing")
				.price(BigDecimal.valueOf(5000))
				.type("test")
				.build();
		product3 = Product.builder()
				.id("00003")
				.category("testing 2")
				.price(BigDecimal.valueOf(1000))
				.type("test")
				.build();

	}


	@Test
	void testGetProducts() {
		Mockito.when(productRepository.findAll()).thenReturn(List.of(product1, product2, product3));

		List<ProductResponseDto> products = productService.findProducts(null,null,null);
		assertThat(products).isNotNull();
		Mockito.verify(productRepository).findAll();
	}

	static Stream<Arguments> minMax() {
		return Stream.of(
				Arguments.of(new BigDecimal(100), new BigDecimal(2000)),
				Arguments.of(new BigDecimal(1500), new BigDecimal(6000)),
				Arguments.of(new BigDecimal(10), new BigDecimal(500)),
				Arguments.of(new BigDecimal(110), new BigDecimal(100))
		);
	}
	@ParameterizedTest
	@MethodSource("minMax")
	void testGetProducts_minMax(BigDecimal min, BigDecimal max) {
		Assertions.assertDoesNotThrow(()->productService.findProducts(min,max,null));
	}

	static Stream<Arguments> categories(){
		return Stream.of(
				Arguments.of("testing"),
				Arguments.of("watching"),
				Arguments.of("")
		);
	}
	@ParameterizedTest
	@MethodSource("categories")
	void testGetProducts_categories(String category) {
		Assertions.assertDoesNotThrow(()->productService.findProducts(null,null,category));
	}

	@Test
	void testCreateProduct_right(){
		var createProduct = CreateProductDto.builder()
				.price(BigDecimal.valueOf(0.00))
				.category(" ")
				.type(" ")
		.build();
		productService.createProduct(createProduct);
		Mockito.verify(productRepository).save(Mockito.any(Product.class));
	}
	@Test
	void testCreateProduct_expectingNullValue(){
		var createProduct = CreateProductDto.builder()
				.price(new BigDecimal(-100))
				.category("")
				.type("")
				.build();
		ProductResponseDto dto = productService.createProduct( createProduct);
		Assertions.assertNull(dto);
		Mockito.verify(productRepository).save(Mockito.any(Product.class));
	}

	@Test
	void testFindProduct_right(){
		productRepository.save(product1);
		Mockito.when(productRepository.findById("0001"))
						.thenReturn(Optional.of(product1));
		Optional<Product> byId = productRepository.findById("0001");
		assertThat(byId).isPresent();
		Mockito.verify(productRepository).findById(Mockito.anyString());
	}

	@Test
	void testUpdateProduct_right(){
		productRepository.save(product1);
		var updateProduct = UpdateProductDto.builder()
				.price(BigDecimal.valueOf(2))
				.category("m")
				.type("m")
				.build();

		productService.updateProduct("0001",updateProduct);
		Mockito.verify(productRepository).findById("0001");
		Mockito.verify(productRepository).save(Mockito.any(Product.class));
		Mockito.verifyNoMoreInteractions(productRepository);
	}

	static Stream<Arguments> updateProductDto(){
		return Stream.of(
				Arguments.arguments(
						UpdateProductDto.builder()
								.price(BigDecimal.valueOf(-1))
								.category("m")
								.type("m")
								.build()),

				Arguments.arguments(
						UpdateProductDto.builder()
								.price(BigDecimal.valueOf(0))
								.category("")
								.type("")
								.build()),

				Arguments.arguments(
						UpdateProductDto.builder()
								.price(BigDecimal.valueOf(10010101))
								.category(null)
								.type(null)
								.build()),

				Arguments.arguments(
						UpdateProductDto.builder()
								.price(null)
								.category(null)
								.type(null)
								.build()),

				Arguments.arguments(
						UpdateProductDto.builder()
								.price(BigDecimal.valueOf(10000))
								.category("testy")
								.type("zesty")
								.build()),

				Arguments.arguments(
						UpdateProductDto.builder().build() )
		);
	}

	@ParameterizedTest
	@MethodSource("updateProductDto")
	void testUpdateProduct_rightId_expectingIllegalArgsInFields(UpdateProductDto updateProductDto){
		Mockito.when(productRepository.save(Mockito.any(Product.class)))
						.thenReturn(product1);
		productRepository.save(product1);
		Mockito.when(productRepository.findById(Mockito.anyString())).thenReturn(Optional.of(product1));

		Optional<ProductResponseDto> dto = productService.updateProduct("0001", updateProductDto);
		Assertions.assertFalse(dto.isEmpty());

		Mockito.verify(productRepository).findById("0001");
	}

	@Test
	void testUpdateProduct_expectingEmptyObject(){
		productRepository.save(product1);
		var updateProduct = UpdateProductDto.builder()
				.price(BigDecimal.valueOf(2))
				.category("m")
				.type("m")
				.build();

		Assertions.assertTrue(productService.updateProduct("0002", updateProduct).isEmpty());

		Mockito.verify(productRepository).findById("0002");
		Mockito.verify(productRepository).save(Mockito.any(Product.class));
		Mockito.verifyNoMoreInteractions(productRepository);
	}

	@Test
	void testDeleteProduct_right(){
		productRepository.save(product1);
		productService.deleteProductById("0001");
		Mockito.verify(productRepository).deleteById("0001");
		Assertions.assertDoesNotThrow(() -> productService.deleteProductById("0001"));
	}
	@Test
	void testDeleteProduct_expectingIgnoredId(){
		productRepository.save(product1);
		productService.deleteProductById("0002");
		Mockito.verify(productRepository).deleteById("0002");
		Assertions.assertDoesNotThrow(() -> productService.deleteProductById("0002"));
	}
}