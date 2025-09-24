package org.project.shopservice.dtos.onResponse;

import lombok.Builder;

@Builder
public record SendEmailDto(
		String body,
		String subject,
		String to
) {
}
