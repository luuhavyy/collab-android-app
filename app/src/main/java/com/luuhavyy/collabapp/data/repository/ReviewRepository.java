package com.luuhavyy.collabapp.data.repository;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luuhavyy.collabapp.data.model.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewRepository {
    private final DatabaseReference reviewRef;

    public ReviewRepository() {
        reviewRef = FirebaseDatabase.getInstance().getReference("reviews");
    }

    public void getReviewsByProductId(String productId, ReviewCallback callback) {
        reviewRef.orderByChild("productid").equalTo(productId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Review> reviews = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Review r = child.getValue(Review.class);
                            if (r != null) reviews.add(r);
                        }
                        callback.onReviewsLoaded(reviews);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    public interface ReviewCallback {
        void onReviewsLoaded(List<Review> reviews);

        void onError(String error);
    }
}
