package com.luuhavyy.collabapp.data.remote;

import com.google.firebase.database.*;

public class ProductRemoteDataSource {
    private final DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products");

    public void getAllProducts(ValueEventListener listener) {
        productRef.addListenerForSingleValueEvent(listener);
    }

    public void getProductById(String productId, ValueEventListener listener) {
        productRef.child(productId).addListenerForSingleValueEvent(listener);
    }

    public void getProductsByCategory(String category, ValueEventListener listener) {
        Query query = productRef.orderByChild("category").equalTo(category);
        query.addListenerForSingleValueEvent(listener);
    }
}
