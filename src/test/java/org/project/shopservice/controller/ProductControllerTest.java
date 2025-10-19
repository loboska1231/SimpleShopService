package org.project.shopservice.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.shopservice.dtos.onRequest.products.CreateProductDto;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
//@ContextConfiguration
//@WithMockUser(authorities = "ROLE_USER")
@WebMvcTest(ProductController.class)
@Import({SecurityConfig.class, JwtFilter.class})
class ProductControllerTest {
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
	ArgumentCaptor<CreateProductDto> createProductDtoArgumentCaptor;

	@Test
	@SneakyThrows
	void testGetProducts(){
		mockMvc.perform(
				get("/products")
		).andExpect(status().isOk());
	}

	@Test
	@SneakyThrows
	@WithMockUser(roles = "ADMIN")
	void testPostProduct(){
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
//				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
//				.andExpect(jsonPath("$.category").value("testing"))
//				.andExpect(jsonPath("$.type").value("test"))
//				.andExpect(jsonPath("$.price").value(BigDecimal.valueOf(600)));
		verify(productService).createProduct(createProductDtoArgumentCaptor.capture());
		assertEquals("testing",createProductDtoArgumentCaptor.getValue().category());
		assertEquals("test",createProductDtoArgumentCaptor.getValue().type());
		assertEquals(BigDecimal.valueOf(600),createProductDtoArgumentCaptor.getValue().price());
	}

}