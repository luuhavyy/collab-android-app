package com.luuhavyy.collabapp.data.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class ProductFilterSort {
    float minPrice;
    float maxPrice;
    boolean frameGlasses;
    boolean sunglasses;
    String sortBy; // VD: "price_asc", "price_desc", "name_asc",...
}