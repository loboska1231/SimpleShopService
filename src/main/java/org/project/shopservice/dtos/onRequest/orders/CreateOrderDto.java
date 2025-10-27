package org.project.shopservice.dtos.onRequest.orders;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
public record CreateOrderDto(
		@NotBlank(message = "Address is empty!")
		@Length(min = 1, max = 30) String address,
        @Valid @NotEmpty(message = "Items are empty!")
        @Size(min = 1) List<CreateOrderItemDto> items
) {

	public CreateOrderDto tidy(){
		Map<String, CreateOrderItemDto> hash = new HashMap<>();
		List<CreateOrderItemDto> newItems = new ArrayList<>();
		items.forEach(item->{
			var dto = hash.get(item.productId());
			if( dto==null){
				hash.put(item.productId(), item);
				dto = item;
			} else {
				Long amount = dto.amount();
				amount+= item.amount();
				dto.toBuilder().amount(amount).build();
			}
			newItems.add(dto);
		});
		return new CreateOrderDto(this.address, new ArrayList<>(newItems));

	}
}
