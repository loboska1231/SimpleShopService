package org.project.shopservice.dtos.onRequest.orders;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

@Builder
public record CreateOrderDto(
        @NonNull @NotBlank @Length(min = 1, max = 30) String address,
        @NonNull @Size(min = 1) List<CreateOrderItemDto> items
) {
	public boolean hasEmptyFields() {
		return StringUtils.isBlank(address) || CollectionUtils.isEmpty(items);
	}
	public CreateOrderDto tidyOrNull(){
		var list = items.stream()
				.filter(item->StringUtils.isNotBlank(item.productId()) && item.amount() >0 )
				.toList();
		if(CollectionUtils.isNotEmpty(list)){
			return new CreateOrderDto(
					this.address,
					new ArrayList<>(items.stream().filter(ObjectUtils::isNotEmpty).toList())
			);
		}
		else return null;
	}
}
