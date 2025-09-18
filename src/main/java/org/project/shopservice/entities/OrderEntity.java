package org.project.shopservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name= "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String whose;
    private String email;
    private String address;
    @Column(name="total_price")
    private BigDecimal totalPrice;
    private Instant date;
    private String status;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    private List<OrderItemEntity> items;

    public void setTotal(){
        if(!CollectionUtils.isEmpty(items)){
            totalPrice = items
                    .stream()
                    .map(OrderItemEntity::getTotalPrice)
                    .reduce(BigDecimal.ZERO,BigDecimal::add);
        } else totalPrice = BigDecimal.ZERO;
    }
    public void assignOrder(){
        if(!CollectionUtils.isEmpty(items)){
            items.forEach(item -> item.setOrder(this));
        }
    }

}
