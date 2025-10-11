package org.project.shopservice.repository;

import org.project.shopservice.models.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product,String> {

    List<Product> findAllByIdIn(Collection<String> ids);
    @Query(value = "{ $and:[ { price: { $gte: ?0, $lte: ?1  } }, { category: {$regex: ?2, $options: 'i' } } ] }")
    List<Product> findAllByPriceGreaterThanEqualAndPriceLessThanEqualAndCategoryIsIgnoreCase(BigDecimal min, BigDecimal max, String category);
    @Query(value = "{ price: { $gte: ?0, $lte: ?1 }}")
    List<Product> findAllByPriceGreaterThanEqualAndPriceLessThanEqual(BigDecimal min, BigDecimal max);
    List<Product> findAllByPriceLessThanEqual(BigDecimal min);
    List<Product> findAllByPriceGreaterThanEqual(BigDecimal max);
    @Query(value = "{ $and:[ { price: { $gte: ?0} }, { category: {$regex: ?1, $options: 'i' } } ] }")
    List<Product> findAllByPriceLessThanEqualAndCategoryIsIgnoreCase(BigDecimal min, String category);
    @Query(value = "{ $and:[ { price: { $lte: ?0} }, { category: {$regex: ?1, $options: 'i' } } ] }")
    List<Product> findAllByPriceGreaterThanEqualAndCategoryIsIgnoreCase(BigDecimal max, String category);
}
