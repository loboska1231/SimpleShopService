package org.project.shopservice.dtos.onRequest.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;

@Builder
public record UserAuthDto(
        @NonNull @NotBlank String username,
        @NonNull @NotBlank @Length(min = 4) String password
){
}
