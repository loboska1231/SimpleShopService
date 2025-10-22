package org.project.shopservice.controller;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.project.shopservice.dtos.onResponse.ErrorDto;
import org.project.shopservice.exceptions.UserAlreadyExistException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ExceptionAdviserUnitTest {
	@InjectMocks
	private ExceptionAdviser exceptionAdviser;

	@Test
	public void testHandleNullPointerException(){
		ResponseEntity<ErrorDto> response =  exceptionAdviser.handleNullPointerException(new  NullPointerException("null value"));
		assertNotNull(response);
		assertTrue(response.getStatusCode().is4xxClientError());
		assertEquals(400,response.getStatusCode().value()); // 400
		assertNotNull(response.getBody());
		assertEquals("NullPointerException",response.getBody().exceptionType());
		assertEquals("null value",response.getBody().details());
	}

	@Test
	public void testHandleUserAlreadyExistException(){
		ResponseEntity<ErrorDto> response =  exceptionAdviser.handleUserAlreadyExistException(new UserAlreadyExistException("users already exists"));
		assertNotNull(response);
		assertTrue(response.getStatusCode().is4xxClientError());
		assertEquals(409,response.getStatusCode().value()); // 409
		assertNotNull(response.getBody());
		assertEquals("users already exists",response.getBody().details());
	}

	@Test
	public void testHandleUsernameNotFoundException(){
		ResponseEntity<ErrorDto> response =  exceptionAdviser.handleUsernameNotFoundException(new UsernameNotFoundException("not found"));
		assertNotNull(response);
		assertTrue(response.getStatusCode().is4xxClientError());
		assertEquals(404,response.getStatusCode().value()); // 404
		assertNotNull(response.getBody());
		assertEquals("UserNotFoundException",response.getBody().exceptionType());
		assertEquals("not found",response.getBody().details());
	}

	@Test
	public void testHandleIllegalArgumentException(){
		ResponseEntity<ErrorDto> response =  exceptionAdviser.handleIllegalArgumentException(new  IllegalArgumentException("illegal argument"));
		assertNotNull(response);
		assertTrue(response.getStatusCode().is4xxClientError());
		assertEquals(400,response.getStatusCode().value()); // 400
		assertNotNull(response.getBody());
		assertEquals("IllegalArgumentException",response.getBody().exceptionType());
		assertEquals("illegal argument",response.getBody().details());
	}
	@Test
	public void testHandleJwtException(){
		ResponseEntity<ErrorDto> response =  exceptionAdviser.handleJwtException(new JwtException("jwt"));
		assertNotNull(response);
		assertTrue(response.getStatusCode().is5xxServerError());
		assertEquals(500,response.getStatusCode().value()); // 500
		assertNotNull(response.getBody());
		assertEquals("JwtException",response.getBody().exceptionType());
		assertEquals("jwt",response.getBody().details());
	}
	@Test
	public void testHandleNoSuchElementException(){
		ResponseEntity<ErrorDto> response =  exceptionAdviser.handleNoSuchElementException(new NoSuchElementException("no ele"));
		assertNotNull(response);
		assertTrue(response.getStatusCode().is4xxClientError());
		assertEquals(404,response.getStatusCode().value()); // 404
		assertNotNull(response.getBody());
		assertEquals("NoSuchElementException",response.getBody().exceptionType());
		assertEquals("no ele",response.getBody().details());
	}
}