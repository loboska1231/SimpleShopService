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
import org.springframework.transaction.annotation.Transactional;
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
	    CreateOrderDto tidiedDto = dto.tidy();
		if(CollectionUtils.isEmpty(tidiedDto.items())) throw new IllegalArgumentException("items is null");
		OrderEntity order = orderMapper.toEntity(tidiedDto);
		loadOrderItemsInfo(order);
		order.setTotal();
		order.assignOrder();
		userService.fillFieldsEmailAndWhose(order);
		OrderEntity save = orderRepository.save(order);
		sendTemplate(order,"Order Created!");
		return orderMapper.toDto(save);
	}

    public Optional<OrderResponseDto> findOrderById(Long id) {

	    Optional<OrderEntity> order = orderRepository.findById(id);
	    if(!(order.isPresent() && userService.canAccess(order.get().getEmail()))) return  Optional.empty();
	    return order.map(orderMapper::toDto);
    }

    public void deleteOrderById(Long id) {
        Optional<OrderEntity> order = orderRepository.findById(id);
        if(order.isPresent() && userService.canAccess(order.get().getEmail())) {
            order.get().setStatus("DELETED");
            sendTemplate(order.get(),"Order Deleted!");
            orderRepository.delete(order.get());
        }
    }
	@Transactional
    public Optional<OrderResponseDto> updateOrder(Long order_id, UpdateOrderDto dto) {
        Optional<OrderEntity> order = orderRepository.findById(order_id);
	    if(order.isPresent()&& userService.canAccess(order.get().getEmail()) && !dto.isEmpty() ){
		    OrderEntity  entity = order.get();
		    deleteItems(entity, dto);
		    orderMapper.updateOrder(entity, dto);
		    loadOrderItemsInfo(entity);
		    entity.setTotal();
		    entity.assignOrder();
		    OrderEntity save = orderRepository.save(entity);

		    sendTemplate(save,"Order updated!");
		    return Optional.of(  orderMapper.toDto(save));
	    }
        else return order.map(orderMapper::toDto);
    }
	private OrderEntity deleteItems(OrderEntity order, UpdateOrderDto dto){
		if(!CollectionUtils.isEmpty(order.getItems())){
			List<String> onDel = dto.onDelete();
			List<UpdateOrderItemDto> onUpd = dto.updateItems();
			boolean emptyToDdelete = CollectionUtils.isEmpty(onDel);
			boolean emptyToUpdate = CollectionUtils.isEmpty(onUpd);
			if(!emptyToDdelete ) {
				Set<String> idsToDelete = new HashSet<>(onDel);
				Set<String> idsToUpdate =
						!emptyToUpdate ? onUpd
								.stream()
								.map(UpdateOrderItemDto::productId)
								.collect(Collectors.toSet())
						: Collections.emptySet();

				boolean contains = Collections.disjoint(idsToDelete, idsToUpdate);
				if(contains) {
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
                throw new IllegalArgumentException("No products found with these ids : " + ids);
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
