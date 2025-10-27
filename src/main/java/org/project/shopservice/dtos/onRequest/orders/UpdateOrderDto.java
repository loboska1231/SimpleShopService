package org.project.shopservice.dtos.onRequest.orders;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Builder
public record UpdateOrderDto(
        String address,
        List<String> onDelete,
        List<UpdateOrderItemDto> updateItems
){
	@Schema(hidden = true)
	public boolean isEmpty(){
		return CollectionUtils.isEmpty(onDelete) &&  CollectionUtils.isEmpty(updateItems) && address==null;
	}
}
