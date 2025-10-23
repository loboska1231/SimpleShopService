package org.project.shopservice.dtos.onRequest.products;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Builder
public record CreateProductDto(
		@NotBlank (message = "Category is empty!")
		@Length(min = 1, max = 30) String category,

		@NotNull(message = "Price is null!")
		@DecimalMin(value = "1") BigDecimal price,

		@NotBlank(message = "Type is empty!")
		@Length(min = 1, max = 30) String type
) { }
