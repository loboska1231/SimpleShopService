package org.project.shopservice.repository;

import org.project.shopservice.models.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product,String> {
    List<Product> findAllByIdIn(Collection<String> ids);
    List<Product> findAllWherePriceLessThanEqualAndPriceGreaterThanEqualAndCategoryIsIgnoreCase(BigDecimal min, BigDecimal max, String category);
    List<Product> findAllWherePriceLessThanEqualAndPriceGreaterThanEqual(BigDecimal min, BigDecimal max);
    List<Product> findAllWherePriceLessThanEqual(BigDecimal min);
    List<Product> findAllWherePriceGreaterThanEqual(BigDecimal max);
    List<Product> findAllWherePriceLessThanEqualAndCategoryIsIgnoreCase(BigDecimal min,String category);
    List<Product> findAllWherePriceGreaterThanEqualAndCategoryIsIgnoreCase(BigDecimal max,String category);

}
