package org.dev.bigData.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegionStats {
    private String region;
    private long totalProducts;
    private double averagePrice;
    private double minPrice;
    private double maxPrice;
    private double totalRevenue;


}