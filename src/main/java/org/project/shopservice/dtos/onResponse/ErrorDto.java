package org.project.shopservice.dtos.onResponse;

import lombok.Builder;

import java.time.Instant;
import java.util.Map;

@Builder
public record ErrorDto(
		String exceptionType,
		Instant date,
		Map<String,Object> details
) {
}
