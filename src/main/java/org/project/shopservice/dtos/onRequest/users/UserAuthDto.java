package org.project.shopservice.dtos.onRequest.users;

import io.micrometer.common.util.StringUtils;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record UserAuthDto(
        @NotBlank(message = "Email is empty!")
        @Email(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "Not an email!")
        String email,
        @NotBlank(message = "Password is empty!")
        @Length(min = 4) String password
){ }
