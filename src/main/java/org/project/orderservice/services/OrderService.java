package org.project.orderservice.services;

import lombok.RequiredArgsConstructor;
import org.project.orderservice.dtos.onRequest.orders.CreateOrderDto;
import org.project.orderservice.dtos.onRequest.orders.UpdateOrderDto;
import org.project.orderservice.dtos.onResponse.OrderResponseDto;
import org.project.orderservice.entities.OrderEntity;
import org.project.orderservice.entities.ProductEntity;
import org.project.orderservice.mapper.OrderMapper;
import org.project.orderservice.mapper.ProductMapper;
import org.project.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;

    public List<OrderResponseDto> findOrders() {
        return orderRepository.findAll().stream().map(orderMapper::toDto).toList();
    }

    public OrderResponseDto createOrder(CreateOrderDto dto) {
        OrderEntity order = orderMapper.toEntity(dto);

        List<ProductEntity> products = dto.products().stream().map(productMapper::toEntity).toList();
        order.setProducts(products);
        order.setTotalPrice(order.getTotal());

        OrderEntity save = orderRepository.save(order);

        return orderMapper.toDto(save);
    }

    public Optional<OrderResponseDto> findOrderById(Integer id) {
        return  orderRepository.findById(id).map(orderMapper::toDto);
    }

    public void deleteOrderById(Integer id) {
        orderRepository.deleteById(id);
    }

    @Transactional
    public OrderResponseDto updateOrder(Integer order_id, UpdateOrderDto dto) {
        OrderEntity order = orderRepository.findById(order_id).get();
        List<ProductEntity> products = order.getProducts();
        if(dto.address() != null) {
            order.setAddress(dto.address());
        }
        if(!dto.onDelete().isEmpty()) {
            List<Integer> ids = dto.onDelete();
            ids.forEach(id->{
                products.removeIf(product -> product.getId().equals(id));
            });
        }
        order.setProducts(products);
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }
}
