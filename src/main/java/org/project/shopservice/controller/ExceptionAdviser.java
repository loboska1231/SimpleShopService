package org.project.shopservice.controller;

import io.jsonwebtoken.JwtException;
import org.project.shopservice.dtos.onResponse.ErrorDto;
import org.project.shopservice.exceptions.UserAlreadyExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ExceptionAdviser {

	/*
	 NullPointerException
	 IllegalArgumentException
	 NoSuchElementException
	 UsernameNotFoundException
	 JwtException
	 UserAlreadyExistException
	 MethodArgumentNotValidException
	 AccessDeniedException
	 */

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		HashMap<String, String> errors = new HashMap<>();

		e.getBindingResult().getAllErrors().forEach(error -> {
			String field = ((FieldError) error).getField();
			String message = error.getDefaultMessage();
			errors.put(field, message);
		});
		ErrorDto dto = ErrorDto.builder()
				.date(Instant.now())
				.exceptionType("MethodArgumentNotValidException")
				.details(errors)
				.build();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(dto);
	}
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorDto> handleAccessDeniedException(AccessDeniedException e) {
		HashMap<String, String> errors = new HashMap<>();

		errors.put("message", e.getMessage());
		ErrorDto dto = ErrorDto.builder()
				.date(Instant.now())
				.exceptionType("AccessDeniedException")
				.details(errors)
				.build();
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(dto);
	}

	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<ErrorDto> handleNullPointerException(NullPointerException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(
						ErrorDto.builder()
								.date(Instant.now())
								.exceptionType("NullPointerException")
								.details(Map.of("message", e.getMessage()))
								.build()
				);
	}

	@ExceptionHandler(UserAlreadyExistException.class)
	public ResponseEntity<ErrorDto> handleUserAlreadyExistException(UserAlreadyExistException e) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(
						ErrorDto.builder()
								.date(Instant.now())
								.exceptionType("UserAlreadyExistException")
								.details(Map.of("message", e.getMessage()))
								.build()
				);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorDto> handleIllegalArgumentException(IllegalArgumentException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(
						ErrorDto.builder()
								.date(Instant.now())
								.exceptionType("IllegalArgumentException")
								.details(Map.of("message", e.getMessage()))
								.build()
				);
	}

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<ErrorDto> handleNoSuchElementException(NoSuchElementException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(
						ErrorDto.builder()
								.date(Instant.now())
								.exceptionType("NoSuchElementException")
								.details(Map.of("message", e.getMessage()))
								.build()
				);
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<ErrorDto> handleUsernameNotFoundException(UsernameNotFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(
						ErrorDto.builder()
								.date(Instant.now())
								.exceptionType("UserNotFoundException")
								.details(Map.of("message", e.getMessage()))
								.build()
				);
	}

	@ExceptionHandler(JwtException.class)
	public ResponseEntity<ErrorDto> handleJwtException(JwtException e) {

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(
						ErrorDto.builder()
								.date(Instant.now())
								.exceptionType("JwtException")
								.details(Map.of("message", e.getMessage()))
								.build()
				);
	}
}
