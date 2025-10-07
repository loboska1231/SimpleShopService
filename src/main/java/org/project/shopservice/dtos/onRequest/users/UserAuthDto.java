package org.project.shopservice.dtos.onRequest.users;

import io.micrometer.common.util.StringUtils;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;

@Builder
public record UserAuthDto(
        @NonNull @NotBlank String email,
        @NonNull @NotBlank @Length(min = 4) String password
){
	public boolean isValid() {
		return StringUtils.isNotBlank(email) && (StringUtils.isNotBlank(password) && password.length() >= 4);
	}
}
