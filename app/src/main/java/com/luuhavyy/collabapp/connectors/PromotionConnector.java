package com.luuhavyy.collabapp.connectors;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import com.luuhavyy.collabapp.data.model.Promotion;

public class PromotionConnector {
    private final DatabaseReference databaseReference;

    public PromotionConnector() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("promotions");
    }

    public interface PromotionLoadListener {
        void onPromotionsLoaded(List<Promotion> promotions);
        void onError(String message);
    }

    public void getPromotionsForUser(String userId, PromotionLoadListener listener) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Promotion> userPromotions = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        String id = snapshot.getKey();
                        String discountType = snapshot.child("discounttype").getValue(String.class);

                        // Handle discount value conversion safely
                        Object discountValueObj = snapshot.child("discountvalue").getValue();
                        long discountValue = 0L;

                        if (discountValueObj instanceof Long) {
                            discountValue = (Long) discountValueObj;
                        } else if (discountValueObj instanceof Double) {
                            discountValue = ((Double) discountValueObj).longValue();
                        } else if (discountValueObj instanceof Integer) {
                            discountValue = ((Integer) discountValueObj).longValue();
                        }

                        String categoryId = snapshot.child("categoryid").getValue(String.class);
                        String promoUserId = snapshot.child("userid").getValue(String.class);

                        if ((promoUserId == null || promoUserId.equals(userId) || promoUserId.isEmpty())) {
                            Promotion promotion = new Promotion();
                            promotion.setPromotionid(id);
                            promotion.setDiscounttype(discountType);
                            promotion.setDiscountvalue(discountValue);
                            promotion.setCategoryid(categoryId);
                            promotion.setUserid(promoUserId);
                            userPromotions.add(promotion);
                        }
                    } catch (Exception e) {
                        Log.e("PromotionConnector", "Error parsing promotion", e);
                    }
                }
                listener.onPromotionsLoaded(userPromotions);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(databaseError.getMessage());
            }
        });
    }

    public void getPromotionById(String promotionId, PromotionLoadListener listener) {
        DatabaseReference promotionsRef = FirebaseDatabase.getInstance().getReference("promotions");
        promotionsRef.child(promotionId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Promotion promotion = dataSnapshot.getValue(Promotion.class);
                if (promotion != null) {
                    List<Promotion> promotions = new ArrayList<>();
                    promotions.add(promotion);
                    listener.onPromotionsLoaded(promotions);
                } else {
                    listener.onPromotionsLoaded(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onError(databaseError.getMessage());
            }
        });
    }

    public void markPromotionAsUsed(String promotionId, final PromotionMarkListener listener) {
        databaseReference.child(promotionId).child("isused").setValue(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onMarkSuccess();
                    } else {
                        listener.onMarkFailed(task.getException() != null ?
                                task.getException().getMessage() : "Unknown error");
                    }
                });
    }

    public interface PromotionMarkListener {
        void onMarkSuccess();
        void onMarkFailed(String errorMessage);
    }
}