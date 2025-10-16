package org.project.shopservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.shopservice.dtos.onRequest.orders.CreateOrderDto;
import org.project.shopservice.dtos.onRequest.orders.UpdateOrderDto;
import org.project.shopservice.dtos.onRequest.orders.UpdateOrderItemDto;
import org.project.shopservice.dtos.onResponse.OrderResponseDto;
import org.project.shopservice.dtos.onResponse.ProductResponseDto;
import org.project.shopservice.dtos.onResponse.SendEmailDto;
import org.project.shopservice.entities.OrderEntity;
import org.project.shopservice.entities.OrderItemEntity;
import org.project.shopservice.mapper.OrderMapper;
import org.project.shopservice.mapper.ProductMapper;
import org.project.shopservice.models.Product;
import org.project.shopservice.repository.OrderRepository;
import org.project.shopservice.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final UserService userService;
    private final EmailService emailService;

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;

    public List<OrderResponseDto> findOrders() {
        return orderRepository.findAll().stream().map(orderMapper::toDto).toList();
    }

    public OrderResponseDto createOrder(CreateOrderDto dto) {
		OrderResponseDto response = null;
        if(!dto.hasEmptyFields()){
	        CreateOrderDto tidiedDto = dto.tidyOrNull();
			if(tidiedDto!=null){
				OrderEntity order = orderMapper.toEntity(tidiedDto);
				loadOrderItemsInfo(order);
				order.setTotal();
				order.assignOrder();
				userService.fillFieldsEmailAndWhose(order);
				OrderEntity save = orderRepository.save(order);
				sendTemplate(order,"Order Created!");
				response = orderMapper.toDto(save);
			}
        }
		 return response;
    }

    public OrderResponseDto findOrderById(Long id) {
        Optional<OrderEntity> order = orderRepository.findById(id);
        return order.isPresent() ? order.map(orderMapper::toDto).get() : null;
    }

    public void deleteOrderById(Long id) {
        Optional<OrderEntity> order = orderRepository.findById(id);
        if(order.isPresent()) {
            order.get().setStatus("DELETED");
            sendTemplate(order.get(),"Order Deleted!");
            orderRepository.delete(order.get());
        }
    }

    public OrderResponseDto updateOrder(Long order_id, UpdateOrderDto dto) {
        OrderEntity order = orderRepository.findById(order_id).orElseThrow();   // NoSuchElementException
	    if(!dto.isEmpty()){
		    deleteItems(order, dto);
		    loadOrderItemsInfo(order);
		    orderMapper.updateOrder(order, dto);
		    order.setTotal();
		    order.assignOrder();
		    OrderEntity save = orderRepository.save(order);

		    sendTemplate(save,"Order updated!");
		    return orderMapper.toDto(save);
	    }
        else return orderMapper.toDto(order);
    }
	private OrderEntity deleteItems(OrderEntity order, UpdateOrderDto dto){
		if(!CollectionUtils.isEmpty(order.getItems())){
			boolean emptyToDdelete = CollectionUtils.isEmpty(dto.onDelete());
			boolean emptyToUpdate = CollectionUtils.isEmpty(dto.updateItems());
			if(!emptyToDdelete ) {
				Set<String> idsToDelete = new HashSet<>(dto.onDelete());
				Set<String> idsToUpdate =
						!emptyToUpdate ? dto.updateItems()
								.stream()
								.map(UpdateOrderItemDto::productId)
								.collect(Collectors.toSet())
						: Collections.emptySet();

				boolean contains = !Collections.disjoint(idsToDelete, idsToUpdate);
				if(!contains) {
					List<OrderItemEntity> items = order.getItems();
					idsToDelete.forEach(id -> items.removeIf(item -> item.getProductId().equals(id)));
					order.setItems(items);
				}
			}
		}
		return order;
	}

    private OrderEntity loadOrderItemsInfo(OrderEntity order) {
        List<OrderItemEntity> items = order.getItems();
        if(!CollectionUtils.isEmpty(items)) {
            Set<String> ids = items.stream().map(OrderItemEntity::getProductId).collect(Collectors.toSet());
            List<Product> listOfFoundProducts = productRepository.findAllByIdIn(ids);
            if(listOfFoundProducts.isEmpty()){
                throw new IllegalArgumentException("No products with these ids : " + ids);
            }
            Map<String, ProductResponseDto> products = listOfFoundProducts.stream()
                    .map(productMapper::toResponse)
                    .collect(Collectors.toMap(ProductResponseDto::id, Function.identity()));
            items.forEach(item -> {
	            ProductResponseDto temp = products.get(item.getProductId());
	            item.setPrice(temp.price());
				item.setCategoryAndType(temp.category()+"; "+ temp.type());
            });
            order.setItems(items);
        }
        return order;
    }

    private void sendTemplate(OrderEntity order, String subject) {
        emailService.sendEmail(SendEmailDto.builder()
                .to(order.getEmail())
                .subject(subject)
                .templateName("order-notification")
                .contextData(Map.of(
                        "whose", order.getWhose(),
                        "email", order.getEmail(),
                        "address", order.getAddress(),
                        "totalPrice", order.getTotalPrice(),
                        "date", order.getDate(),
                        "status", order.getStatus(),
                        "items", order.getItems()
                ))
                .build());
    }
}
