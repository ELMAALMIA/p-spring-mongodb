package org.dev.bigData.repositories;

import org.dev.bigData.models.Product;
import org.dev.bigData.models.RegionStats;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByRegion(String region);

    @Aggregation(pipeline = {
            "{ $group: { _id: '$region', avgPrice: { $avg: '$price' } } }"
    })
    List<RegionStats> getAveragePriceByRegion();
}