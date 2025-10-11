package org.project.shopservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.project.shopservice.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {
//	@Autowired
//	private MockMvc mockMvc;
//
//	@MockitoBean
//	private AuthService authService;
//
//	@Test
//	void authenticateUser() {
//	}
//
//	@Test
//	void registerUser() {
//	}
//
//	@Test
//	void refreshToken() {
//	}
}