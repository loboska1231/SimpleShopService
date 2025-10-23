package org.project.shopservice.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.shopservice.services.AuthService;
import org.project.shopservice.services.OrderService;
import org.project.shopservice.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({OrderController.class, ProductController.class, AuthenticationController.class})
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class ExceptionAdviserIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OrderService orderService;
	@MockitoBean
	private ProductService productService;
	@MockitoBean
	private AuthService authService;

	@Test
	@SneakyThrows
	void testFindProductById_ExpectingNoSuchElementException(){
		mockMvc.perform(
				get("/products/{id}","1")
		).andExpect(status().isNotFound())
				.andExpect(jsonPath("$").hasJsonPath())
				.andExpect(jsonPath("$.exceptionType").isNotEmpty())
				.andExpect(jsonPath("$.exceptionType").value("NoSuchElementException"))
				.andExpect(jsonPath("$.details..message").value("Product not found"))
		;
		verify(productService).findProductById(anyString());
	}
	@Test
	@SneakyThrows
	void testCreateProduct_MethodArgumentNotValidException(){
		mockMvc.perform(
						post("/products")
								.contentType(MediaType.APPLICATION_JSON)
								.content("""
										{}
										""")
				).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$").hasJsonPath())
				.andExpect(jsonPath("$.exceptionType").isNotEmpty())
				.andExpect(jsonPath("$.exceptionType").value("MethodArgumentNotValidException"))
				.andExpect(jsonPath("$.details..category").value("Category is empty!"))
				.andExpect(jsonPath("$.details..price").value("Price is null!"))
				.andExpect(jsonPath("$.details..type").value("Type is empty!"))
		;
		verifyNoInteractions(productService);
	}
	@Test
	@SneakyThrows
	void testUpdateProduct_NoSuchElementException(){
		mockMvc.perform(
						patch("/products/{id}","0i1")
								.contentType(MediaType.APPLICATION_JSON)
								.content("""
										{
											"address": ""
										}
										""")
				).andExpect(status().isNotFound())
				.andExpect(jsonPath("$").hasJsonPath())
				.andExpect(jsonPath("$.exceptionType").isNotEmpty())
				.andExpect(jsonPath("$.exceptionType").value("NoSuchElementException"))
				.andExpect(jsonPath("$.details..message").value("Product not found"))
		;
		verify(productService).updateProduct(eq("0i1"), any());
	}

	@Test
	@SneakyThrows
	void testCreateOrder_MethodArgumentNotValidException_addressAndItemsNull(){
		mockMvc.perform(
				post("/orders")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{}
								""")
		).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$").hasJsonPath())
				.andExpect(jsonPath("$.exceptionType").isNotEmpty())
				.andExpect(jsonPath("$.exceptionType").value("MethodArgumentNotValidException"))
				.andExpect(jsonPath("$.details..address").value("Address is empty!"))
				.andExpect(jsonPath("$.details..items").value("Items are empty!"));
	}
	@Test
	@SneakyThrows
	void testCreateOrder_MethodArgumentNotValidException_addressNull(){
		mockMvc.perform(
						post("/orders")
								.contentType(MediaType.APPLICATION_JSON)
								.content("""
										{
										"items": [{
										
										}]
										}""")
				).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$").hasJsonPath())
				.andExpect(jsonPath("$.exceptionType").isNotEmpty())
				.andExpect(jsonPath("$.exceptionType").value("MethodArgumentNotValidException"))
				.andExpect(jsonPath("$.details..address").value("Address is empty!"))
				.andExpect(jsonPath("$['details']['items[0].productId']").value("Product Id is empty!"))
				.andExpect(jsonPath("$['details']['items[0].amount']").value("Amount is empty!"));
	}
	@Test
	@SneakyThrows
	void testGetOrder_NoSuchElementException(){
		mockMvc.perform(
						get("/orders/{id}",1)
				).andExpect(status().isNotFound())
				.andExpect(jsonPath("$").hasJsonPath())
				.andExpect(jsonPath("$.exceptionType").isNotEmpty())
				.andExpect(jsonPath("$.exceptionType").value("NoSuchElementException"))
				.andExpect(jsonPath("$.details..message").value("Order not found"));
	}
	@Test
	@SneakyThrows
	void testUpdateOrder_NoSuchElementException(){
		mockMvc.perform(
						patch("/orders/{id}",1)
								.contentType(MediaType.APPLICATION_JSON)
								.content("""
										{
											"address": "new"
										}
										""")
				).andExpect(status().isNotFound())
				.andExpect(jsonPath("$").hasJsonPath())
				.andExpect(jsonPath("$.exceptionType").isNotEmpty())
				.andExpect(jsonPath("$.exceptionType").value("NoSuchElementException"))
				.andExpect(jsonPath("$.details..message").value("Order not found"));
	}
	@Test
	@SneakyThrows
	void testSignupUser_MethodArgumentNotValidException(){
		mockMvc.perform(
						post("/auth/{endpoint}", "signup")
								.contentType(MediaType.APPLICATION_JSON)
								.content("""
										{}
										"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$").hasJsonPath())
				.andExpect(jsonPath("$.exceptionType").isNotEmpty())
				.andExpect(jsonPath("$.exceptionType").value("MethodArgumentNotValidException"))
				.andExpect(jsonPath("$.details..firstName").value("First name is empty!"))
				.andExpect(jsonPath("$.details..lastName").value("Last name is empty!"))
				.andExpect(jsonPath("$.details..email").value("Email is empty!"))
				.andExpect(jsonPath("$.details..password").value("Password is empty!"))
		;
	}

	@Test
	@SneakyThrows
	void testRefresh_MethodArgumentNotValidException(){
		mockMvc.perform(
						post("/auth/{endpoint}", "refresh")
								.contentType(MediaType.APPLICATION_JSON)
								.content("""
										{
											"refreshToken": ""
										}
										"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$").hasJsonPath())
				.andExpect(jsonPath("$.exceptionType").isNotEmpty())
				.andExpect(jsonPath("$.exceptionType").value("MethodArgumentNotValidException"))
				.andExpect(jsonPath("$.details..refreshToken").value("Refresh token is empty!"))
		;
	}
	@Test
	@SneakyThrows
	void testSignin_MethodArgumentNotValidException(){
//		when(authService.authenticateUser(any(UserAuthDto.class))).thenReturn(null);
		mockMvc.perform(
						post("/auth/{endpoint}", "signin")
								.contentType(MediaType.APPLICATION_JSON)
								.content("""
										{
											"email": "",
											"password": ""
										}
										"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$").hasJsonPath())
				.andExpect(jsonPath("$.exceptionType").isNotEmpty())
				.andExpect(jsonPath("$.exceptionType").value("MethodArgumentNotValidException"))
				.andExpect(jsonPath("$.details..email").value("Email is empty!"))
				.andExpect(jsonPath("$.details..password").exists())
		;
	}

}