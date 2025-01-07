//package org.dev.bigData;
//

//
//import java.util.List;
//import java.util.Random;
//
//import static org.hamcrest.Matchers.any;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//@ExtendWith(MockitoExtension.class)
//public class ProductServicePerformanceTestMock {
//
//    @Mock
//    private MongoTemplate mongoTemplate;
//
//    @InjectMocks
//    private ProductService productService;
//
//    private final Random random = new Random();
//    private static final String[] REGIONS = {"NA", "EU", "ASIA", "SA", "AF"};
//    private static final String[] PRODUCT_PREFIXES = {
//            "Laptop", "Smartphone", "Tablet", "Monitor", "Keyboard",
//            "Mouse", "Headphones", "Speaker", "Camera", "Printer"
//    };
//
//    @BeforeEach
//    void setup() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testBulkInsert() {
//        List<Product> products = generateTestProducts(10000);
//        when(mongoTemplate.save(any(Product.class))).thenReturn(new Product());
//
//        long startTime = System.currentTimeMillis();
//        products.forEach(productService::saveProduct);
//        long duration = System.currentTimeMillis() - startTime;
//
//        verify(mongoTemplate, times(10000)).save(any(Product.class));
//        log.info("Bulk insert of 10000 products took {} ms", duration);
//    }
//
//    @Test
//    void testBatchInsert() {
//        List<Product> products = generateTestProducts(10000);
//        when(mongoTemplate.insertAll(anyList())).thenReturn(products);
//
//        long startTime = System.currentTimeMillis();
//        productService.saveAllProducts(products);
//        long duration = System.currentTimeMillis() - startTime;
//
//        verify(mongoTemplate).insertAll(eq(products));
//        log.info("Batch insert of 10000 products took {} ms", duration);
//    }
//
//    @Test
//    void testRegionQuery() {
//        when(mongoTemplate.find(any(Query.class), eq(Product.class)))
//                .thenReturn(Arrays.asList(new Product(), new Product()));
//
//        long startTime = System.currentTimeMillis();
//        for (String region : REGIONS) {
//            List<Product> regionProducts = productService.findByRegionAndPriceRange(
//                    region, 100.0, 1000.0);
//            log.info("Found {} products in region {}", regionProducts.size(), region);
//        }
//        long duration = System.currentTimeMillis() - startTime;
//
//        verify(mongoTemplate, times(REGIONS.length))
//                .find(any(Query.class), eq(Product.class));
//        log.info("Region queries took {} ms", duration);
//    }
//
//    @Test
//    void testPaginationPerformance() {
//        Page<Product> mockPage = new PageImpl<>(
//                Arrays.asList(new Product(), new Product())
//        );
//        when(mongoTemplate.count(any(Query.class), eq(Product.class)))
//                .thenReturn(100L);
//        when(mongoTemplate.find(any(Query.class), eq(Product.class)))
//                .thenReturn(mockPage.getContent());
//
//        long startTime = System.currentTimeMillis();
//        for (int i = 0; i < 5; i++) {
//            var page = productService.findAllProducts(PageRequest.of(i, 20));
//            log.info("Page {} has {} items", i, page.getContent().size());
//        }
//        long duration = System.currentTimeMillis() - startTime;
//
//        verify(mongoTemplate, times(5))
//                .find(any(Query.class), eq(Product.class));
//        log.info("Pagination queries took {} ms", duration);
//    }
//
//    @Test
//    void testSearchPerformance() {
//        List<Product> mockResults = Arrays.asList(
//                new Product(), new Product(), new Product()
//        );
//        when(mongoTemplate.find(any(Query.class), eq(Product.class)))
//                .thenReturn(mockResults);
//
//        long startTime = System.currentTimeMillis();
//        List<String> regions = List.of("NA", "EU");
//        List<Product> searchResults = productService.searchProducts(
//                "Laptop",
//                200.0,
//                800.0,
//                regions
//        );
//        long duration = System.currentTimeMillis() - startTime;
//
//        verify(mongoTemplate).find(any(Query.class), eq(Product.class));
//        log.info("Search query returned {} results and took {} ms",
//                searchResults.size(), duration);
//    }
//
//    // Helper methods remain unchanged
//    private List<Product> generateTestProducts(int count) { /* ... */ }
//    private String generateProductName() { /* ... */ }
//    private String generateDescription() { /* ... */ }
//    private double generatePrice() { /* ... */ }
//}