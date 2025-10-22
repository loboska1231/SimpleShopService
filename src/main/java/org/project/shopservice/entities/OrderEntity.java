package org.project.shopservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
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
