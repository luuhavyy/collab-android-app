package com.luuhavyy.collabapp.data.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    String productid;
    String name;
    int price;
    int ratings;
    int stock;
    String image;
    String description;
    String categoryid;
}
