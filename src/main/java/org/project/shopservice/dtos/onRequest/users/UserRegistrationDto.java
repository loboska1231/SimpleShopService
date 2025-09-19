package org.project.shopservice.dtos.onRequest.users;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;

@Builder
public record UserRegistrationDto(
        @NonNull @NotBlank String username,
        @NonNull @NotBlank @Length(min = 4) String password,
        @NonNull @NotBlank @Length(min = 2, max = 40) String firstName,
        @NonNull @NotBlank @Length(min = 2, max = 40) String lastName,
        String role
) {
}
