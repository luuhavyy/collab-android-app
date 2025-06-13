package com.luuhavyy.collabapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.connectors.PromotionConnector;
import com.luuhavyy.collabapp.data.model.CartItem;
import com.luuhavyy.collabapp.data.model.Product;
import com.luuhavyy.collabapp.data.model.Promotion;
import com.luuhavyy.collabapp.ui.adapters.PromotionAdapter;

import java.util.ArrayList;
import java.util.List;

public class VoucherActivity extends AppCompatActivity {
    private ListView listView;
    private ImageView imgBack;
    private PromotionAdapter promotionAdapter;
    private List<Promotion> promotions = new ArrayList<>();
    private PromotionConnector promotionConnector = new PromotionConnector();
    private Button btnApply;
    private List<CartItem> cartItems;
    private List<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);

        Intent intent = getIntent();
        if (intent != null) {
            cartItems = (List<CartItem>) intent.getSerializableExtra("cartItems");
            products = (List<Product>) intent.getSerializableExtra("products");
        }

        listView = findViewById(R.id.listview);
        imgBack = findViewById(R.id.imgBack);
        btnApply = findViewById(R.id.btnApply);

        promotionAdapter = new PromotionAdapter(this, promotions);
        listView.setAdapter(promotionAdapter);

        loadUserPromotions();

        btnApply.setOnClickListener(v -> {
            Promotion selected = promotionAdapter.getSelectedPromotion();

            if (selected == null) {
                Toast.makeText(this, R.string.select_voucher_first, Toast.LENGTH_SHORT).show();
                return;
            }

            if (selected.getCategoryid() != null && !selected.getCategoryid().isEmpty()) {
                boolean hasMatchingProduct = false;
                for (int i = 0; i < cartItems.size(); i++) {
                    if (cartItems.get(i).isSelected() &&
                            products.get(i).getCategoryid().equals(selected.getCategoryid())) {
                        hasMatchingProduct = true;
                        break;
                    }
                }

                if (!hasMatchingProduct) {
                    Toast.makeText(this,
                            getString(R.string.voucher_category_restriction, selected.getCategoryid()),
                            Toast.LENGTH_LONG).show();
                    return;
                }
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("selected_promotion", selected);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        imgBack.setOnClickListener(v -> finish());
    }

    private void loadUserPromotions() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference mappingRef = FirebaseDatabase.getInstance().getReference("firebaseUidToUserId");
            mappingRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userId = dataSnapshot.getValue(String.class);
                        promotionConnector.getPromotionsForUser(userId, new PromotionConnector.PromotionLoadListener() {
                            @Override
                            public void onPromotionsLoaded(List<Promotion> loadedPromotions) {
                                promotions.clear();
                                if (loadedPromotions != null) {
                                    promotions.addAll(loadedPromotions);
                                }
                                promotionAdapter.notifyDataSetChanged();

                                if (promotions.isEmpty()) {
                                    Toast.makeText(VoucherActivity.this, R.string.no_vouchers, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(String message) {
                                Toast.makeText(VoucherActivity.this, getString(R.string.error_loading_vouchers, message), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(VoucherActivity.this, R.string.user_mapping_not_found, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(VoucherActivity.this,
                            getString(R.string.error_loading_user_mapping, databaseError.getMessage()),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, R.string.user_not_authenticated, Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}