package org.dev.bigData.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Sharded;

@Document(collection = "products")
@Getter
@Setter
@NoArgsConstructor

@Sharded(shardKey = "region")
public class Product {
    @Id
    private String id;

    @Indexed
    private String name;

    private String description;

    @Indexed
    private double price;

    @Indexed
    private String region;
}