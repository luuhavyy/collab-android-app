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
public class Review {
    String reviewid;
    String orderid;
    String productid;
    int rating;
    String review;
    String timestamp;
    String userid;
}
