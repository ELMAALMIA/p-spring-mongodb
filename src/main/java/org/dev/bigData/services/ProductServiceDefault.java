package org.dev.bigData.services;


import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.dev.bigData.models.Product;
import org.dev.bigData.models.RegionStats;
import org.dev.bigData.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceDefault implements ProductService {

    private final ProductRepository productRepository;

    private final MongoTemplate mongoTemplate;



    public Product saveProduct(Product product) {
        validateProduct(product);
        return productRepository.save(product);
    }

    public List<Product> saveAllProducts(List<Product> products) {
        products.forEach(this::validateProduct);
        return productRepository.saveAll(products);
    }

    public Optional<Product> findById(String id) {
        return productRepository.findById(id);
    }

    public Page<Product> findAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public List<Product> findByRegionAndPriceRange(String region, Double minPrice, Double maxPrice) {
        Query query = new Query(Criteria.where("region").is(region));

        if (minPrice != null && maxPrice != null) {
            query.addCriteria(Criteria.where("price").gte(minPrice).lte(maxPrice));
        } else if (minPrice != null) {
            query.addCriteria(Criteria.where("price").gte(minPrice));
        } else if (maxPrice != null) {
            query.addCriteria(Criteria.where("price").lte(maxPrice));
        }

        return mongoTemplate.find(query, Product.class);
    }

    public List<RegionStats> getRegionStatistics() {
        return productRepository.getAveragePriceByRegion();
    }

    public Product updateProduct(Product product) {
        if (!productRepository.existsById(product.getId())) {
            throw new IllegalArgumentException("Product not found with id: " + product.getId());
        }
        validateProduct(product);
        return productRepository.save(product);
    }

    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    public List<Product> searchProducts(String name, Double minPrice, Double maxPrice, List<String> regions) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        if (StringUtils.hasText(name)) {
            criteriaList.add(Criteria.where("name").regex(name, "i"));
        }

        if (minPrice != null && maxPrice != null) {
            criteriaList.add(Criteria.where("price").gte(minPrice).lte(maxPrice));
        } else if (minPrice != null) {
            criteriaList.add(Criteria.where("price").gte(minPrice));
        } else if (maxPrice != null) {
            criteriaList.add(Criteria.where("price").lte(maxPrice));
        }

        if (regions != null && !regions.isEmpty()) {
            criteriaList.add(Criteria.where("region").in(regions));
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        return mongoTemplate.find(query, Product.class);
    }

    private void validateProduct(Product product) {
        List<String> errors = new ArrayList<>();

        if (!StringUtils.hasText(product.getName())) {
            errors.add("Product name is required");
        }

        if (product.getPrice() == 0.0|| product.getPrice() < 0) {
            errors.add("Product price must be non-negative");
        }

        if (!StringUtils.hasText(product.getRegion())) {
            errors.add("Product region is required");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Product validation failed: " + String.join(", ", errors));
        }
    }

    // Méthodes additionnelles pour les statistiques et l'analyse

    public List<Product> findTopProductsByRegion(String region, int limit) {
        Query query = new Query(Criteria.where("region").is(region))
                .limit(limit)
                .with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "price"));
        return mongoTemplate.find(query, Product.class);
    }

    public long getProductCountByRegion(String region) {
        Query query = new Query(Criteria.where("region").is(region));
        return mongoTemplate.count(query, Product.class);
    }

    public double getAveragePriceByRegion(String region) {
        Query query = new Query(Criteria.where("region").is(region));
        List<org.dev.bigData.models.Product> products = mongoTemplate.find(query, Product.class);

        return products.stream()
                .mapToDouble(Product::getPrice)
                .average()
                .orElse(0.0);
    }
}