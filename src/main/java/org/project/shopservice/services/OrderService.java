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
import org.project.shopservice.repository.OrderRepository;
import org.project.shopservice.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        OrderEntity order = orderMapper.toEntity(dto);
        loadOrderItemsInfo(order);
        order.setTotal();
        order.assignOrder();
        userService.fillFieldsUsernameAndWhose(order);
        OrderEntity save = orderRepository.save(order);

//        emailService.sendEmail(SendEmailDto.builder()
//                        .to(order.getEmail())
//                        .subject("Order created!")
//                        .body(order.toString())
//                .build());

        return orderMapper.toDto(save);
    }

    public Optional<OrderResponseDto> findOrderById(Integer id) {
        return  orderRepository.findById(id).map(orderMapper::toDto);
    }

    public void deleteOrderById(Integer id) {
        orderRepository.deleteById(id);
    }


    public OrderResponseDto updateOrder(Integer order_id, UpdateOrderDto dto) {
        OrderEntity order = orderRepository.findById(order_id).orElseThrow();   // NoSuchElementException
        deleteItems(order, dto);
        orderMapper.updateOrder(order, dto);
        loadOrderItemsInfo(order);
        order.setTotal();
        order.assignOrder();
        OrderEntity save = orderRepository.save(order);

        return orderMapper.toDto(save);
    }

    private OrderEntity deleteItems(OrderEntity order, UpdateOrderDto dto){
        List<String> idsToDelete = dto.onDelete();
        List<String> idsToUpdate = dto.updateItems().stream().map(UpdateOrderItemDto::productId).toList();
        boolean contains =  false;
        if(!idsToUpdate.isEmpty()){
            contains = idsToDelete.stream().anyMatch(idsToUpdate::contains);
        }
        if(!CollectionUtils.isEmpty(idsToDelete) && !contains) {
            List<OrderItemEntity> items = order.getItems();
            idsToDelete.forEach(id -> {
                items.removeIf(item -> item.getProductId().equals(id));
            });
            order.setItems(items);
        }

        return order;
    }

    private OrderEntity loadOrderItemsInfo(OrderEntity order) {
        List<OrderItemEntity> items = order.getItems();
        if(!CollectionUtils.isEmpty(items)) {
            List<String> ids = items.stream().map(OrderItemEntity::getProductId).toList();
            Map<String, ProductResponseDto> products = productRepository.findAllByIdIn(ids)
                    .stream()
                    .map(productMapper::toResponse)
                    .collect(Collectors.toMap(ProductResponseDto::id, Function.identity()));
            // in the future, I am going to add exception handler on exc(NotFound)

            items.forEach(item -> item.setPrice(products.get(item.getProductId()).price()));
            order.setItems(items);
        }
        return order;
    }
}
