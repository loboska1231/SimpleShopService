package org.project.shopservice.dtos.onRequest.users;

import io.micrometer.common.util.StringUtils;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;

@Builder
public record UserRegistrationDto(
        @NonNull @NotBlank String email,
        @NonNull @NotBlank @Length(min = 4) String password,
        @NonNull @NotBlank @Length(min = 2, max = 40) String firstName,
        @NonNull @NotBlank @Length(min = 2, max = 40) String lastName,
        String role
) {
	public boolean hasEmptyRequiredFields(){
		return StringUtils.isBlank(email)
				|| StringUtils.isBlank(password)
				|| StringUtils.isBlank(firstName)
				|| StringUtils.isBlank(lastName);
	}
}
