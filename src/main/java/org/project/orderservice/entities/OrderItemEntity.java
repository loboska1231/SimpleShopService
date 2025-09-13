package org.project.orderservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "order_items")
public class OrderItemEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "product_id")
    private String productId;

    private BigDecimal price;

    private Long amount;
    @ToString.Exclude
    @ManyToOne(cascade = {})
    @JoinColumn(name = "order_id")
    private OrderEntity order;
}
