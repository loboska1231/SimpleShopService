package org.project.shopservice.repository;

import org.bson.types.Decimal128;
import org.project.shopservice.models.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product,String> {

    List<Product> findAllByIdIn(Collection<String> ids);

    @Query(value = "{ $and:[ { price: { $gte: ?0, $lte: ?1  } }, { category: {$regex: '^?2$', $options: 'mi' } } ] }")
    List<Product> findAllByPriceGreaterThanEqualAndPriceLessThanEqualAndCategoryIsIgnoreCase(Decimal128 min, Decimal128 max, String category);

    @Query(value = "{ price: { $gte: ?0, $lte: ?1 }}")
    List<Product> findAllByPriceGreaterThanEqualAndPriceLessThanEqual(Decimal128 min, Decimal128 max);

    List<Product> findAllByPriceLessThanEqual(Decimal128 max);

    List<Product> findAllByPriceGreaterThanEqual(Decimal128 min);

    @Query(value = "{ $and:[ { price: { $lte: ?0} }, { category: {$regex: '^?1$', $options: 'mi' } } ] }")
    List<Product> findAllByPriceLessThanEqualAndCategoryIsIgnoreCase(Decimal128 max, String category);

    @Query(value = "{ $and:[ { price: { $gte: ?0} }, { category: {$regex: '^?1$', $options: 'mi' } } ] }")
    List<Product> findAllByPriceGreaterThanEqualAndCategoryIsIgnoreCase(Decimal128 min, String category);

    List<Product> findAllByCategoryIgnoreCase(String category);
}
