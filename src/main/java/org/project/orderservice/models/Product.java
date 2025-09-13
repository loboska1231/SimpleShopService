package org.project.orderservice.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("products")
public class Product {
    @MongoId
    private String id;

    private String category;

    private BigDecimal price;

    private String type;

}
