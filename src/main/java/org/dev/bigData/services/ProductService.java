package org.dev.bigData.services;

import org.dev.bigData.models.Product;
import org.dev.bigData.models.RegionStats;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    Product saveProduct(Product product);

    List<Product> saveAllProducts(List<Product> products);

    Optional<Product> findById(String id);

    Page<Product> findAllProducts(Pageable pageable);

    List<Product> findByRegionAndPriceRange(String region, Double minPrice, Double maxPrice);

    List<RegionStats> getRegionStatistics();

    Product updateProduct(Product product);

    void deleteProduct(String id);

    List<Product> searchProducts(String name, Double minPrice, Double maxPrice, List<String> regions);

    List<Product> findTopProductsByRegion(String region, int limit);

    long getProductCountByRegion(String region);

    double getAveragePriceByRegion(String region);
}