package org.project.shopservice.dtos.onRequest.users;

import jakarta.validation.constraints.NotBlank;

public record RefreshToken(
		@NotBlank(message = "Refresh token is empty!")
		String refreshToken
) { }
