package org.project.shopservice.dtos.onResponse;

import lombok.Builder;

import java.util.Map;

@Builder
public record SendEmailDto(
		String templateName,
		String subject,
		String to,
		Map<String,Object> contextData
) {
}
