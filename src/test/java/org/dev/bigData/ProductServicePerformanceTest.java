package org.dev.bigData;

import lombok.extern.log4j.Log4j2;
import org.dev.bigData.models.Product;
import org.dev.bigData.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@SpringBootTest
@Log4j2
class ProductServicePerformanceTest {

    @Autowired
    private ProductService productService;

    private final Random random = new Random();
    private static final String[] REGIONS = {"NA", "EU", "ASIA", "SA", "AF"};
    private static final String[] PRODUCT_PREFIXES = {
            "Laptop", "Smartphone", "Tablet", "Monitor", "Keyboard",
            "Mouse", "Headphones", "Speaker", "Camera", "Printer"
    };

    @BeforeEach
    void cleanup() {
    }

    @Test
    void testBulkInsert() {
        List<Product> products = generateTestProducts(10000);
        long startTime = System.currentTimeMillis();

        products.forEach(productService::saveProduct);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        log.info("Bulk insert of 10000 products took {} ms", duration);
    }

    @Test
    void testBatchInsert() {
        List<Product> products = generateTestProducts(10000);
        long startTime = System.currentTimeMillis();

        productService.saveAllProducts(products);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        log.info("Batch insert of 10000 products took {} ms", duration);
    }

    @Test
    void testRegionQuery() {

        List<Product> products = generateTestProducts(10000);
        productService.saveAllProducts(products);

        long startTime = System.currentTimeMillis();


        for (String region : REGIONS) {
            List<Product> regionProducts = productService.findByRegionAndPriceRange(
                    region,
                    100.0,
                    1000.0
            );
            log.info("Found {} products in region {}", regionProducts.size(), region);
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        log.info("Region queries took {} ms", duration);
    }

    @Test
    void testPaginationPerformance() {

        List<Product> products = generateTestProducts(10000);
        productService.saveAllProducts(products);

        long startTime = System.currentTimeMillis();


        for (int i = 0; i < 5; i++) {
            var page = productService.findAllProducts(PageRequest.of(i, 20));
            log.info("Page {} has {} items", i, page.getContent().size());
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        log.info("Pagination queries took {} ms", duration);
    }

    @Test
    void testSearchPerformance() {

        List<Product> products = generateTestProducts(10000);
        productService.saveAllProducts(products);

        long startTime = System.currentTimeMillis();


        List<String> regions = List.of("NA", "EU");
        List<Product> searchResults = productService.searchProducts(
                "Laptop",
                200.0,
                800.0,
                regions
        );

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        log.info("Search query returned {} results and took {} ms",
                searchResults.size(), duration);
    }

    private List<Product> generateTestProducts(int count) {
        List<Product> products = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Product product = new Product();
            product.setName(generateProductName());
            product.setDescription(generateDescription());
            product.setPrice(generatePrice());
            product.setRegion(REGIONS[random.nextInt(REGIONS.length)]);
            products.add(product);
        }

        return products;
    }

    private String generateProductName() {
        String prefix = PRODUCT_PREFIXES[random.nextInt(PRODUCT_PREFIXES.length)];
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        return prefix + " " + suffix;
    }

    private String generateDescription() {
        return "Description for product " + UUID.randomUUID().toString().substring(0, 16);
    }

    private double generatePrice() {
        return 100 + random.nextDouble() * 900;
    }
}