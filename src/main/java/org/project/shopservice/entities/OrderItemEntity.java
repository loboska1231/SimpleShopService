package org.project.shopservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "order_items")
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    private String productId;

    private String categoryAndType;

    private BigDecimal price;

    private Long amount;
    @ToString.Exclude
    @ManyToOne(cascade = {})
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    public BigDecimal getTotalPrice() {
        return price.multiply(new BigDecimal(amount));
    }
}
