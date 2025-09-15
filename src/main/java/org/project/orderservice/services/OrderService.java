package org.project.orderservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.orderservice.dtos.onRequest.orders.CreateOrderDto;
import org.project.orderservice.dtos.onRequest.orders.UpdateOrderDto;
import org.project.orderservice.dtos.onRequest.orders.UpdateOrderItemDto;
import org.project.orderservice.dtos.onResponse.OrderItemResponseDto;
import org.project.orderservice.dtos.onResponse.OrderResponseDto;
import org.project.orderservice.dtos.onResponse.ProductResponseDto;
import org.project.orderservice.entities.OrderEntity;
import org.project.orderservice.entities.OrderItemEntity;
import org.project.orderservice.models.Product;
import org.project.orderservice.mapper.OrderMapper;
import org.project.orderservice.mapper.ProductMapper;
import org.project.orderservice.repository.OrderRepository;
import org.project.orderservice.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        OrderEntity save = orderRepository.save(order);

        return orderMapper.toDto(save);
    }

    public Optional<OrderResponseDto> findOrderById(Integer id) {
        return  orderRepository.findById(id).map(orderMapper::toDto);
    }

    public void deleteOrderById(Integer id) {
        orderRepository.deleteById(id);
    }


    public OrderResponseDto updateOrder(Integer order_id, UpdateOrderDto dto) {
        OrderEntity order = orderRepository.findById(order_id).get();

        deleteItems(order, dto);
        orderMapper.updateOrderItems(order, dto);
        loadOrderItemsInfo(order);
        order.setTotal();
        order.assignOrder();
        OrderEntity save = orderRepository.save(order);

        return orderMapper.toDto(save);
    }

    public OrderEntity deleteItems(OrderEntity order, UpdateOrderDto dto){
        List<String> idsToDelete = dto.onDelete();
        log.info("ids to delete: {}", idsToDelete);
        List<String> idsToUpdate = dto.updateItems().stream().map(UpdateOrderItemDto::productId).toList();
        boolean contains =  false;
        if(!idsToUpdate.isEmpty()){
            contains = idsToDelete.stream().anyMatch(idsToUpdate::contains);
        }
        log.info("contains: {}", contains);
        if(!CollectionUtils.isEmpty(idsToDelete) && !contains) {
            List<OrderItemEntity> items = order.getItems();
            idsToDelete.forEach(id -> {
                items.removeIf(item -> item.getProductId().equals(id));
                log.info("id to delete: {}", id);
            });
            log.info("items: {}", items);
            order.setItems(items);
        }

        return order;
    }

    public OrderEntity loadOrderItemsInfo(OrderEntity order) {
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
