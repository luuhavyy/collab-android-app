package com.luuhavyy.collabapp.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luuhavyy.collabapp.data.model.Promotion;

public class PromotionViewModel extends ViewModel {
    private final MutableLiveData<Promotion> promotionLiveData = new MutableLiveData<>();

    public LiveData<Promotion> getPromotion() { return promotionLiveData; }

    public void loadPromotionById(String promotionId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("promotions");
        ref.orderByChild("promotionid").equalTo(promotionId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Promotion promotion = null;
                        for (DataSnapshot child : snapshot.getChildren()) {
                            promotion = child.getValue(Promotion.class);
                            break;
                        }
                        promotionLiveData.setValue(promotion);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        promotionLiveData.setValue(null);
                    }
                });
    }
}

