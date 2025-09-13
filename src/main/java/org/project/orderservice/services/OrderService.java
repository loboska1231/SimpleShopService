package org.project.orderservice.services;

import lombok.RequiredArgsConstructor;
import org.project.orderservice.dtos.onRequest.orders.CreateOrderDto;
import org.project.orderservice.dtos.onRequest.orders.UpdateOrderDto;
import org.project.orderservice.dtos.onResponse.OrderItemResponseDto;
import org.project.orderservice.dtos.onResponse.OrderResponseDto;
import org.project.orderservice.entities.OrderEntity;
import org.project.orderservice.entities.OrderItemEntity;
import org.project.orderservice.models.Product;
import org.project.orderservice.mapper.OrderMapper;
import org.project.orderservice.mapper.ProductMapper;
import org.project.orderservice.repository.OrderRepository;
import org.project.orderservice.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
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

//        List<String> ids = order.getItems().stream().map(OrderItemEntity::getProductId).toList();
//        List<Product> products = productRepository.findAllById(ids);


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

        orderRepository.save(order);
        return orderMapper.toDto(order);
    }
}
