package org.project.shopservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.project.shopservice.dtos.onRequest.users.RefreshToken;
import org.project.shopservice.dtos.onRequest.users.UserAuthDto;
import org.project.shopservice.dtos.onRequest.users.UserRegistrationDto;
import org.project.shopservice.dtos.onResponse.ErrorDto;
import org.project.shopservice.dtos.onResponse.TokensDto;
import org.project.shopservice.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Auth",description = "Authorization and authentication URI's for register( /signup ), authorize( /signin ), refresh token ( /refresh )")
@SecurityRequirement(name = "")
public class AuthenticationController {
    private final AuthService authService;
	@Operation(
			operationId = "authorizeUser",
			summary = "Authorize user",
			description = "Authorize user ( signin ) to receive new tokens and send specific requests to other URI's",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					required = true,
					content = @Content(schema = @Schema(implementation = UserAuthDto.class))))
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "OK",
					content = @Content(schema = @Schema(implementation = TokensDto.class))),
			@ApiResponse(
					responseCode = "404",
					description = "BAD_REQUEST",
					content = @Content(schema = @Schema(implementation = ErrorDto.class))
			)
	})
    @PostMapping("/signin")
    public ResponseEntity<TokensDto> authorizeUser(@Validated @RequestBody UserAuthDto dto) {
        return ResponseEntity.ok(authService.authenticateUser(dto));
    }

	@Operation(
			operationId = "registerUser",
			summary = "Register user",
			description = "Register user ( signup ) to save user, receive tokens and send specific requests to other URI's",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					required = true,
					content = @Content(schema = @Schema(implementation = UserRegistrationDto.class))))
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "OK",
					content = @Content(schema = @Schema(implementation = TokensDto.class))),
			@ApiResponse(
					responseCode = "404",
					description = "BAD_REQUEST",
					content = @Content(schema = @Schema(implementation = ErrorDto.class))
			)
	})
    @PostMapping("/signup")
    public ResponseEntity<TokensDto> registerUser(@Validated @RequestBody UserRegistrationDto dto) {
        return ResponseEntity.ok(authService.registrateUser(dto));
    }

	@Operation(
			operationId = "refresh",
			summary = "Refresh tokens",
			description = "Refresh tokens to update user's field refreshToken, receive new tokens and send specific requests to other URI's",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					required = true,
					content = @Content(schema = @Schema(implementation = RefreshToken.class))))
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "OK",
					content = @Content(schema = @Schema(implementation = TokensDto.class))),
			@ApiResponse(
					responseCode = "404",
					description = "BAD_REQUEST",
					content = @Content(schema = @Schema(implementation = ErrorDto.class))
			)
	})
    @PostMapping("/refresh")
    public ResponseEntity<TokensDto> refreshToken(@Validated @RequestBody RefreshToken refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken.refreshToken())); // =D
    }
}
