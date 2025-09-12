package org.project.orderservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String category;

    private BigDecimal price;

    private String type;

    @ToString.Exclude
    @ManyToOne(cascade = {})
    @JoinColumn(name="order_id")
    private OrderEntity order;

}
