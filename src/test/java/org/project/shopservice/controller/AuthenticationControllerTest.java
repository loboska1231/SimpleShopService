package org.project.shopservice.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.project.shopservice.dtos.onRequest.users.UserAuthDto;
import org.project.shopservice.dtos.onRequest.users.UserRegistrationDto;
import org.project.shopservice.dtos.onResponse.TokensDto;
import org.project.shopservice.enums.Roles;
import org.project.shopservice.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AuthService authService;
	private final TokensDto tokens = new TokensDto("access-token-for-test", "refresh-token-for-test");

	@Captor
	ArgumentCaptor<UserRegistrationDto> userRegistrationCaptor;
	@Captor
	ArgumentCaptor<UserAuthDto> userAuthCaptor;

	@Test
	@SneakyThrows
	void testSignupUser() {
		when(authService.registrateUser(any(UserRegistrationDto.class)))
				.thenReturn(tokens);
		mockMvc.perform(
			post("/auth/{endpoint}", "signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
						"firstName":"test",
						"lastName":"tet",
						"email":"test@gmail.com",
						"password":"1111",
						"role":"USER"
					}
					"""))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$").hasJsonPath())
		.andExpect(jsonPath("$.accessToken").exists())
		.andExpect(jsonPath("$.accessToken").value(tokens.accessToken()))
		.andExpect(jsonPath("$.refreshToken").exists())
		.andExpect(jsonPath("$.refreshToken").value(tokens.refreshToken()))
		;

		verify(authService).registrateUser(userRegistrationCaptor.capture());
		assertEquals("test", userRegistrationCaptor.getValue().firstName());
		assertEquals(Roles.USER.toString(), userRegistrationCaptor.getValue().role());
		assertEquals("1111", userRegistrationCaptor.getValue().password());
		assertEquals("test@gmail.com", userRegistrationCaptor.getValue().email());
		assertEquals("tet", userRegistrationCaptor.getValue().lastName());
	}

	@Test
	@SneakyThrows
	void testSignupUser_withOutRole() {
		when(authService.registrateUser(any(UserRegistrationDto.class)))
				.thenReturn(tokens);
		mockMvc.perform(
			post("/auth/{endpoint}", "signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
						"firstName":"test",
						"lastName":"tet",
						"email":"test@gmail.com",
						"password":"1111"
					}
					"""))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$").hasJsonPath())
		.andExpect(jsonPath("$.accessToken").exists())
		.andExpect(jsonPath("$.accessToken").value(tokens.accessToken()))
		.andExpect(jsonPath("$.refreshToken").exists())
		.andExpect(jsonPath("$.refreshToken").value(tokens.refreshToken()))
		;

		verify(authService).registrateUser(userRegistrationCaptor.capture());
		assertEquals("test", userRegistrationCaptor.getValue().firstName());
		assertNull(userRegistrationCaptor.getValue().role());
		assertEquals("1111", userRegistrationCaptor.getValue().password());
		assertEquals("test@gmail.com", userRegistrationCaptor.getValue().email());
		assertEquals("tet", userRegistrationCaptor.getValue().lastName());
	}

	@Test
	@SneakyThrows
	void testSigninUser() {
		when(authService.authenticateUser(any(UserAuthDto.class)))
				.thenReturn(tokens);
		mockMvc.perform(
						post("/auth/{endpoint}", "signin")
								.contentType(MediaType.APPLICATION_JSON)
								.content("""
										{
											"email":"test@gmail.com",
											"password":"1111"
										}
										"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").hasJsonPath())
				.andExpect(jsonPath("$.accessToken").exists())
				.andExpect(jsonPath("$.accessToken").value(tokens.accessToken()))
				.andExpect(jsonPath("$.refreshToken").exists())
				.andExpect(jsonPath("$.refreshToken").value(tokens.refreshToken()))
		;

		verify(authService).authenticateUser(userAuthCaptor.capture());
		assertEquals("1111", userAuthCaptor.getValue().password());
		assertEquals("test@gmail.com", userAuthCaptor.getValue().email());
	}


	@Test
	@SneakyThrows
	void refreshToken() {
		when(authService.refreshToken(eq("refresh-token-for-test")))
				.thenReturn(tokens);
		mockMvc.perform(
						post("/auth/{endpoint}", "refresh")
								.contentType(MediaType.APPLICATION_JSON)
								.content("""
										{
											"refreshToken":"refresh-token-for-test"
										}
										"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").hasJsonPath())
				.andExpect(jsonPath("$.accessToken").exists())
				.andExpect(jsonPath("$.accessToken").value(tokens.accessToken()))
				.andExpect(jsonPath("$.refreshToken").exists())
				.andExpect(jsonPath("$.refreshToken").value(tokens.refreshToken()))
		;

		verify(authService).refreshToken(eq("refresh-token-for-test"));
	}

	@Test
	@SneakyThrows
	void testRefreshToken_NullBody() {
		when(authService.refreshToken(eq("refresh-token-for-test")))
				.thenReturn(tokens);
		mockMvc.perform(
						post("/auth/{endpoint}", "refresh")
								.contentType(MediaType.APPLICATION_JSON)
								.content("""
										{
											"refreshToken":"refresh"
										}
										"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").doesNotHaveJsonPath())
		;

		verify(authService).refreshToken(eq("refresh"));
	}
}