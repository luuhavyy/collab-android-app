package com.luuhavyy.collabapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.luuhavyy.collabapp.data.model.Cart;
import com.luuhavyy.collabapp.data.model.CartItem;
import com.luuhavyy.collabapp.data.model.Product;
import com.luuhavyy.collabapp.data.model.Promotion;
import com.luuhavyy.collabapp.ui.adapters.PromotionAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoucherActivity extends AppCompatActivity {
    private ListView listView;
    private ImageView imgBack;
    private PromotionAdapter promotionAdapter;
    private List<Promotion> promotions = new ArrayList<>();
    private PromotionConnector promotionConnector = new PromotionConnector();
    private Button btnApply;
    private String cartid;
    private String userId;
    private List<CartItem> cartItems = new ArrayList<>();
    private Map<String, Product> productMap = new HashMap<>();
    private boolean isCartItemsLoaded = false;
    private boolean isProductsLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);

        // Kiểm tra cartid đầu tiên
        cartid = getIntent().getStringExtra("cartid");
        if (cartid == null || cartid.isEmpty()) {
            Toast.makeText(this, R.string.cart_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Nhận dữ liệu từ intent
        Serializable selectedItemsExtra = getIntent().getSerializableExtra("selected_items");
        Serializable selectedProductsExtra = getIntent().getSerializableExtra("selected_products");

        if (selectedItemsExtra != null && selectedItemsExtra instanceof ArrayList
                && selectedProductsExtra != null && selectedProductsExtra instanceof ArrayList) {
            cartItems = (ArrayList<CartItem>) selectedItemsExtra;
            List<Product> selectedProducts = (ArrayList<Product>) selectedProductsExtra;
            productMap.clear();
            for (Product product : selectedProducts) {
                productMap.put(product.getProductid(), product);
            }
        } else {
            loadCartItems(); // Chỉ load khi không có dữ liệu từ intent
        }

        // Khởi tạo view
        listView = findViewById(R.id.listview);
        imgBack = findViewById(R.id.imgBack);
        btnApply = findViewById(R.id.btnApply);

        promotionAdapter = new PromotionAdapter(this, promotions);
        listView.setAdapter(promotionAdapter);

        // Thiết lập sự kiện
        btnApply.setOnClickListener(v -> {
            Promotion selected = promotionAdapter.getSelectedPromotion();

            if (selected == null) {
                Toast.makeText(this, R.string.select_voucher_first, Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate voucher
            if (selected.getUserid() != null && !selected.getUserid().isEmpty()
                    && selected.getCategoryid() != null && !selected.getCategoryid().isEmpty()) {
                boolean hasMatchingProduct = false;
                for (CartItem item : cartItems) {
                    Product product = productMap.get(item.getProductid());
                    if (product != null && selected.getCategoryid().equals(product.getCategoryid())) {
                        hasMatchingProduct = true;
                        break;
                    }
                }

                if (!hasMatchingProduct) {
                    Toast.makeText(this, R.string.voucher_not_for_products, Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("selected_promotion_id", selected.getPromotionid());
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        imgBack.setOnClickListener(v -> finish());
    }

    private void loadCartItems() {
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("carts").child(cartid);
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Cart cart = dataSnapshot.getValue(Cart.class);
                if (cart != null) {
                    cartItems = cart.getProducts() != null ? cart.getProducts() : new ArrayList<>();
                    List<String> productIds = new ArrayList<>();
                    for (CartItem item : cartItems) {
                        productIds.add(item.getProductid());
                    }
                    isCartItemsLoaded = true;
                    loadProducts(productIds);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("VoucherActivity", "Error loading cart: " + databaseError.getMessage());
                isCartItemsLoaded = true;
                loadUserPromotions();
            }
        });
    }


    private void loadProducts(List<String> productIds) {
        if (productIds.isEmpty()) {
            isProductsLoaded = true;
            loadUserPromotions();
            return;
        }

        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");
        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productMap.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null && productIds.contains(product.getProductid())) {
                        productMap.put(product.getProductid(), product);
                    }
                }
                isProductsLoaded = true;
                loadUserPromotions();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("VoucherActivity", "Error loading products: " + databaseError.getMessage());
                isProductsLoaded = true;
                loadUserPromotions(); // Still try to load promotions
            }
        });
    }

    private boolean validateVoucherForCart(Promotion promotion) {
        // Skip validation if voucher is not user-specific or has no category restriction
        if (promotion.getUserid() == null || promotion.getUserid().isEmpty() ||
                promotion.getCategoryid() == null || promotion.getCategoryid().isEmpty()) {
            return true;
        }

        // Check if product data is loaded
        if (!isProductsLoaded) {
            Log.d("VoucherValidation", "Product data not loaded yet");
            return false;
        }

        // Debug logging
        Log.d("VoucherValidation", "Checking voucher for category: " + promotion.getCategoryid());
        Log.d("VoucherValidation", "Product map size: " + productMap.size());

        // Check if any cart item's product matches the voucher category
        for (CartItem item : cartItems) {
            Product product = productMap.get(item.getProductid());
            if (product != null) {
                Log.d("VoucherValidation", "Checking product: " + product.getProductid() +
                        " with category: " + product.getCategoryid());
                if (promotion.getCategoryid().equals(product.getCategoryid())) {
                    Log.d("VoucherValidation", "Match found for product: " + product.getProductid());
                    return true;
                }
            }
        }
        Log.d("VoucherValidation", "No matching products found");
        return false;
    }

    private void loadUserPromotions() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference mappingRef = FirebaseDatabase.getInstance().getReference("firebaseUidToUserId");
            mappingRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        userId = dataSnapshot.getValue(String.class);
                        promotionConnector.getPromotionsForUser(userId, new PromotionConnector.PromotionLoadListener() {
                            @Override
                            public void onPromotionsLoaded(List<Promotion> loadedPromotions) {
                                if (loadedPromotions != null) {
                                    promotions.clear();
                                    promotions.addAll(loadedPromotions);
                                    promotionAdapter.notifyDataSetChanged();

                                    if (promotions.isEmpty()) {
                                        Toast.makeText(VoucherActivity.this,
                                                R.string.no_vouchers,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onError(String message) {
                                Toast.makeText(VoucherActivity.this,
                                        getString(R.string.error_loading_vouchers) + message,
                                        Toast.LENGTH_SHORT).show();
                                Log.e("VoucherActivity", "Error loading promotions: " + message);
                                finish();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(VoucherActivity.this,
                            getString(R.string.database_error) + databaseError.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }
}