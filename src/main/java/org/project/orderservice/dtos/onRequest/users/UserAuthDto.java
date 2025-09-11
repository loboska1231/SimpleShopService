package org.project.orderservice.dtos.onRequest.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;

@Builder
public record UserAuthDto(
        @NonNull @NotBlank @Email(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$") String email,
        @NonNull @NotBlank @Length(min = 4) String password
){
}
