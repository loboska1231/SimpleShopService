package org.project.shopservice.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.shopservice.dtos.onRequest.products.CreateProductDto;
import org.project.shopservice.dtos.onRequest.products.UpdateProductDto;
import org.project.shopservice.dtos.onResponse.ProductResponseDto;
import org.project.shopservice.models.Product;
import org.project.shopservice.repository.ProductRepository;
import org.project.shopservice.security.config.SecurityConfig;
import org.project.shopservice.security.filters.JwtFilter;
import org.project.shopservice.security.utils.JwtUtil;
import org.project.shopservice.services.AuthService;
import org.project.shopservice.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ProductController.class)
@Import({SecurityConfig.class, JwtFilter.class})
class ProductControllerTest {

	@MockitoBean
	private ProductRepository productRepository;

	@MockitoBean
	private ProductService productService;
	@MockitoBean
	private JwtUtil jwtUtil;
	@MockitoBean
	private AuthService authService;
	@MockitoBean
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MockMvc mockMvc;

	@Captor
	ArgumentCaptor<CreateProductDto> createProductDtoCaptor;
	@Captor
	ArgumentCaptor<UpdateProductDto> updateProductDtoCaptor;

	private final List<ProductResponseDto> products = List.of(
			ProductResponseDto.builder().type("test 1").category("testing 1").price(BigDecimal.valueOf(100)).id("0x1").build(),
			ProductResponseDto.builder().type("test 2").category("testing 1").price(BigDecimal.valueOf(150)).id("0x2").build(),
			ProductResponseDto.builder().type("test 3").category("testing 1").price(BigDecimal.valueOf(200)).id("0x3").build(),
			ProductResponseDto.builder().type("test 4").category("testing 2").price(BigDecimal.valueOf(500)).id("0x4").build(),
			ProductResponseDto.builder().type("test 5").category("testing 2").price(BigDecimal.valueOf(550)).id("0x5").build()
	);

	static Stream<Arguments> argsForGetProductsTest() {
		return Stream.of(
				Arguments.of("50", "170", "testing 1"),
				Arguments.of("200", "", "testing 1"),
				Arguments.of("", "550", "testing 2"),
				Arguments.of("50", "500", ""),
				Arguments.of("", "250", ""),
				Arguments.of("50", "", ""),
				Arguments.of("", "-1", ""),
				Arguments.of("", "", "testing 1")
		);
	}

	@ParameterizedTest
	@MethodSource("argsForGetProductsTest")
	@SneakyThrows
	void testGetProducts_parametrized_200ExpectingNotEmptyBody(String min, String max, String category) {
		mockMvc.perform(
				get("/products")
						.param("min", min)
						.param("max", max)
						.param("category", category)
		).andExpect(status().isOk())
		;
		verify(productService).findProducts(nullable(BigDecimal.class), nullable(BigDecimal.class), nullable(String.class));
	}

	@Test
	@SneakyThrows
	void testGetProducts_200ExpectingEmptyBody() {
		mockMvc.perform(
				get("/products")
		).andExpect(status().isOk());

		List<ProductResponseDto> products = productService.findProducts(null, null, null);
		assertTrue(products.isEmpty());
		verify(productService, times(2)).findProducts(nullable(BigDecimal.class), nullable(BigDecimal.class), nullable(String.class));
	}

	@Test
	@SneakyThrows
	void testGetProducts_200ExpectingNotEmptyBody() {
		when(productService.findProducts(nullable(BigDecimal.class), nullable(BigDecimal.class), nullable(String.class)))
				.thenReturn(products);

		mockMvc.perform(
						get("/products")
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0]..id").value("0x1"))
				.andExpect(jsonPath("$[0]..type").value("test 1"))
				.andExpect(jsonPath("$[0]..category").value("testing 1"))
				.andExpect(jsonPath("$[0]..price").value(100))
				.andExpect(jsonPath("$[3]..id").value("0x4"))
				.andExpect(jsonPath("$[3]..type").value("test 4"))
				.andExpect(jsonPath("$[3]..category").value("testing 2"))
				.andExpect(jsonPath("$[3]..price").value(500))
		;
		verify(productService).findProducts(nullable(BigDecimal.class), nullable(BigDecimal.class), nullable(String.class));
	}

	@Test
	@SneakyThrows
	void testGetProduct_200() {
		when(productService.findProductById(anyString()))
				.thenReturn(Optional.of(ProductResponseDto
						.builder()
						.id("0x1")
						.price(BigDecimal.valueOf(599))
						.type("test")
						.category("testing")
						.build()));

		mockMvc.perform(
						get("/products/0x1")
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("0x1"))
				.andExpect(jsonPath("$.type").value("test"))
				.andExpect(jsonPath("$.category").value("testing"))
				.andExpect(jsonPath("$.price").value(599))
		;
		verify(productService).findProductById(anyString());
	}

	@Test
	@SneakyThrows
	void testGetProduct_ExpectingNoSuchElement() {
		mockMvc.perform(
				get("/products/{id}", "0x1")
		).andExpect(status().isNotFound());
		verify(productService).findProductById(eq("0x1"));
	}

	static Stream<Arguments> argForCreateProduct() {
		return Stream.of(
				Arguments.of(0, "", ""),
				Arguments.of(100, "", ""),
				Arguments.of(100, "test", ""),
				Arguments.of(100, "  ", "  "),
				Arguments.of(100, "test", "  "),
				Arguments.of(100, "test", "testing1testing2testing3testing4testing5"),
				Arguments.of(100, "test1test2test3test4test5test6test7", "testing"),
				Arguments.of(100, "", "testing"),
				Arguments.of(0, "test", "testing")
		);
	}

	@ParameterizedTest
	@MethodSource("argForCreateProduct")
	@WithMockUser(roles = "ADMIN")
	@SneakyThrows
	void testCreateProduct_400ExpectingBadRequest(int price, String category, String type) {
		mockMvc.perform(
				post("/products")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								"category": "%s",
								"type": "%s",
								"price": %d
								}
								""".formatted(category, type, price))
		).andExpect(status().isBadRequest());
		verifyNoInteractions(productService);
	}

	@Test
	@SneakyThrows
	@WithMockUser(roles = "ADMIN")
	void testPostProduct_200ExpectingCreatedObject() {
		mockMvc.perform(
				post("/products")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								"category": "testing",
								"type": "test",
								"price": 600
								}
								""")

		).andExpect(status().isOk());

		verify(productService).createProduct(createProductDtoCaptor.capture());
		assertEquals("testing", createProductDtoCaptor.getValue().category());
		assertEquals("test", createProductDtoCaptor.getValue().type());
		assertEquals(BigDecimal.valueOf(600), createProductDtoCaptor.getValue().price());
	}

	@Test
	@SneakyThrows
	void testPostProduct_403ExpectingForbidden() {
		mockMvc.perform(
				post("/products")
		).andExpect(status().isForbidden());
		verifyNoInteractions(productService);
	}

	static Stream<Arguments> argsForUpdateProduct() {
		return Stream.of(
				Arguments.of("0x1", -1, null, null),
				Arguments.of("0x1", -1, null, "test"),
				Arguments.of("0x1", -1, "category", "test"),
				Arguments.of("0x1", 0, "category", "test"),
				Arguments.of("0x1", 100, "", "test"),
				Arguments.of("0x1", 100, "categorycategorycategorycategorycategorycategory", "test"),
				Arguments.of("0x1", 100, "category", ""),
				Arguments.of("0x1", 100, "category", "testcategorycategorycategorycategorycategorytestttt")


		);
	}

	@ParameterizedTest
	@MethodSource("argsForUpdateProduct")
	@WithMockUser(roles = "ADMIN")
	@SneakyThrows
	void testUpdateProduct_400ExpectingBadRequest(String id, int price, String category, String type) {
		mockMvc.perform(
				patch("/products/{id}", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								"price": %d,
								"category": "%s",
								"type": "%s"
								}
								""".formatted(price, category, type))
		).andExpect(status().isBadRequest());
		verifyNoInteractions(productService);
	}

	static Stream<Arguments> argsForUpdateProductWithWrongId() {
		return Stream.of(
				Arguments.of("0x2", 100, "category", "test"),
				Arguments.of("2", 100, "category", "test")
		);
	}

	@ParameterizedTest
	@MethodSource("argsForUpdateProductWithWrongId")
	@WithMockUser(roles = "ADMIN")
	@SneakyThrows
	void testUpdateProduct_withWrongId_400ExpectingBadRequest(String id, int price, String category, String type) {
		when(productRepository.findById(eq("0x1")))
				.thenReturn(Optional.of(Product.builder()
						.type("test 1")
						.category("testing 1")
						.price(BigDecimal.valueOf(100))
						.id("0x1")
						.build())
				);
		mockMvc.perform(
				patch("/products/{id}", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								"price": %d,
								"category": "%s",
								"type": "%s"
								}
								""".formatted(price, category, type))
		).andExpect(status().isNotFound());
		verify(productService).updateProduct(anyString(),updateProductDtoCaptor.capture());
		assertEquals("category", updateProductDtoCaptor.getValue().category());
		verifyNoMoreInteractions(productService);
	}


	@Test
	@SneakyThrows
	@WithMockUser(roles = "ADMIN")
	void testUpdateProduct_200ExpectingUpdatedObject() {
		when(productService.updateProduct(anyString(), any(UpdateProductDto.class)))
				.thenReturn(Optional.of(ProductResponseDto.builder()
						.id("0x1")
						.price(BigDecimal.valueOf(400))
						.type("updated")
						.category("testing")
						.build()));

		mockMvc.perform(
						patch("/products/{id}", "0x1")
								.contentType(MediaType.APPLICATION_JSON)
								.content("""
										{
											"price": 400,
											"type": "updated"
										}
										""")
				).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("0x1"))
				.andExpect(jsonPath("$.type").value("updated"))
				.andExpect(jsonPath("$.category").value("testing"))
				.andExpect(jsonPath("$.price").value(400));
		verify(productService).updateProduct(eq("0x1"), updateProductDtoCaptor.capture());
	}

	@Test
	@SneakyThrows
	void testUpdateProduct_403ExpectingForbidden() {
		mockMvc.perform(
				patch("/products/{id}", "0x1")
		).andExpect(status().isForbidden());
		verifyNoInteractions(productService);
	}

	@Test
	@SneakyThrows
	@WithMockUser(roles = "ADMIN")
	void testDeleteProduct_204Expected() {
		mockMvc.perform(
				delete("/products/{id}", "0x1")
		).andExpect(status().isNoContent());

		verify(productService).deleteProductById(eq("0x1"));
	}

	@Test
	@SneakyThrows
	void testDeleteProduct_403ExpectedForbidden() {
		mockMvc.perform(
				delete("/products/{id}", "0x1")
		).andExpect(status().isForbidden());
		verifyNoInteractions(productService);
	}

}