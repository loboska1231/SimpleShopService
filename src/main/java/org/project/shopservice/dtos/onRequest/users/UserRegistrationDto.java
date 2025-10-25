package org.project.shopservice.dtos.onRequest.users;

import io.micrometer.common.util.StringUtils;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record UserRegistrationDto(
		@NotBlank(message = "Email is empty!")
		@Email String email,

		@NotBlank(message = "Password is empty!")
		@Length(min = 4) String password,

		@NotBlank(message = "First name is empty!")
		@Length(min = 2, max = 40) String firstName,

		@NotBlank(message = "Last name is empty!")
		@Length(min = 2, max = 40) String lastName,

		String role
) { }
