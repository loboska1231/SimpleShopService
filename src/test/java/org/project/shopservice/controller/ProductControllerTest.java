package org.project.shopservice.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.project.shopservice.dtos.onResponse.ProductResponseDto;
import org.project.shopservice.security.config.SecurityConfig;
import org.project.shopservice.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ProductController.class)
//@AutoConfigureMockMvc
class ProductControllerTest {
//
//	static GrantedAuthority ADMIN = new SimpleGrantedAuthority("ADMIN");
//
//	@MockitoBean
//	ProductService productService;
//
//	@Autowired
//	MockMvc mockMvc;
//
//	@Test
//	@SneakyThrows
//	@WithMockUser(username = "testAdmin", authorities = {"ROLE_ADMIN"})
//	void createProduct(){
//		mockMvc.perform(
//				post("/products")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content("""
//							{
//								"category": "testing",
//								"price": 600.00,
//								"type": "test"
//							}
//						""")
//				)
//				.andExpect(status().isOk())
//		;
//	}
//	@Test
//	@SneakyThrows
//	@WithMockUser(username = "testAdmin", authorities = {"ADMIN"})
//	void getProducts(){
//		mockMvc
//			.perform(
//				get("/products").contentType(MediaType.APPLICATION_JSON))
//				.andExpect(status().isOk())
//
//		;
//	}
}