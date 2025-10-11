package org.project.shopservice.controller;

import io.jsonwebtoken.JwtException;
import org.project.shopservice.dtos.onResponse.ErrorDto;
import org.project.shopservice.exceptions.UserAlreadyExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ExceptionAdviser {

	//*
	// NullPointerException
	// IllegalArgumentException
	// NoSuchElementException
	// UsernameNotFoundException
	// JwtException
	// UserAlreadyExistException
	// *

	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<ErrorDto>handleNullPointerException(NullPointerException e){
		Map<String, Object> details = new HashMap<>();
		details.put("message", e.getMessage());

			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(
						ErrorDto.builder()
							.date(Instant.now())
							.exceptionType("NullPointerException")
							.details(details)
						.build()
					);
	}

	@ExceptionHandler(UserAlreadyExistException.class)
	public ResponseEntity<ErrorDto>handleUserAlreadyExistException(UserAlreadyExistException e){
		Map<String, Object> details = new HashMap<>();
		details.put("message", e.getMessage());

		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(
						ErrorDto.builder()
								.date(Instant.now())
								.exceptionType("UserAlreadyExistException")
								.details(details)
								.build()
				);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorDto>handleIllegalArgumentException(IllegalArgumentException e){
		Map<String, Object> details = new HashMap<>();
		details.put("message", e.getMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(
						ErrorDto.builder()
								.date(Instant.now())
								.exceptionType("IllegalArgumentException")
								.details(details)
								.build()
				);
	}

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<ErrorDto>handleNoSuchElementException(NoSuchElementException e){
		Map<String, Object> details = getDetails(e);
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(
						ErrorDto.builder()
								.date(Instant.now())
								.exceptionType("NoSuchElementException")
								.details(details)
								.build()
				);
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<ErrorDto>handleUsernameNotFoundException(UsernameNotFoundException e){
		Map<String, Object> details =getDetails(e);
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(
						ErrorDto.builder()
								.date(Instant.now())
								.exceptionType("UserNotFoundException")
								.details(details)
								.build()
				);
	}

	@ExceptionHandler(JwtException.class)
	public ResponseEntity<ErrorDto>handleJwtException(JwtException e){
		Map<String, Object> details = new HashMap<>(Map.of("message", e.getMessage()));

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(
						ErrorDto.builder()
								.date(Instant.now())
								.exceptionType("JwtException")
								.details(details)
								.build()
				);
	}

	private Map<String, Object> getDetails(Exception e) {
		Map<String, Object> details = new HashMap<>();
		Arrays.stream(e.getSuppressed()).forEach(suppressed -> {
			String field = suppressed.getClass().getSimpleName();
			String message = suppressed.getMessage();
			details.put(field, message);
		});
		return details;
	}
}
