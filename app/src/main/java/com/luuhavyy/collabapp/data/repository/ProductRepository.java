package com.luuhavyy.collabapp.data.repository;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luuhavyy.collabapp.data.model.ProductFilterSort;

public class ProductRepository {
    private final DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products");

    public void listenToProductsRealtime(ValueEventListener listener) {
        productRef.addValueEventListener(listener);
    }

    public void removeProductListener(ValueEventListener listener) {
        productRef.removeEventListener(listener);
    }

    public void fetchProductsWithFilter(ProductFilterSort filterSort, ValueEventListener listener) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("products");
        ref.orderByChild("price")
                .startAt(filterSort.minPrice)
                .endAt(filterSort.maxPrice)
                .addListenerForSingleValueEvent(listener);
    }
}