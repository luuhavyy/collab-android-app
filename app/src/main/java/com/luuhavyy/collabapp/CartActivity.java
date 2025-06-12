package com.luuhavyy.collabapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import adapters.CartAdapter;
import connectors.CartConnector;
import connectors.PromotionConnector;
import models.Cart;
import models.CartItem;
import models.Product;
import models.Promotion;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnQuantityChangeListener {
    private ListView lvProductCart;
    private TextView txtTotalValue;
    private Button btnCheckoutCart;
    private CheckBox rbuttonAllCart;
    private ImageView imgVoucher, imgRecycleBin;
    private CartAdapter cartAdapter;
    private CartConnector cartConnector;
    private PromotionConnector promotionConnector;

    private List<CartItem> cartItems = new ArrayList<>();
    private List<Product> products = new ArrayList<>();
    private Cart currentCart;
    private Promotion selectedPromotion;
    private double totalAmount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupAdapters();
        setupListeners();
        loadUserCart();
    }

    private void initializeViews() {
        lvProductCart = findViewById(R.id.lvProductCart);
        txtTotalValue = findViewById(R.id.txtTotalValue);
        btnCheckoutCart = findViewById(R.id.btnCheckoutCart);
        rbuttonAllCart = findViewById(R.id.rbuttonAllCart);
        imgVoucher = findViewById(R.id.imgVoucher);
        imgRecycleBin = findViewById(R.id.imgRecycleBin);

        cartConnector = new CartConnector();
        promotionConnector = new PromotionConnector();
    }


    private void setupAdapters() {
        cartAdapter = new CartAdapter(this,cartItems, products, this);
        lvProductCart.setAdapter(cartAdapter);
    }

    private void setupListeners() {
        imgVoucher.setOnClickListener(v -> openVoucherActivity());

        imgRecycleBin.setOnClickListener(v -> deleteSelectedItems());

        btnCheckoutCart.setOnClickListener(v -> checkoutCart());

        rbuttonAllCart.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CartItem item : cartItems) {
                item.setSelected(isChecked);
            }
            cartAdapter.notifyDataSetChanged();
            calculateTotal();
        });
    }

    private void loadUserCart() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference mappingRef = FirebaseDatabase.getInstance().getReference("firebaseUidToUserId");
            mappingRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userId = dataSnapshot.getValue(String.class);
                        loadCartData(userId);
                    } else {
                        Toast.makeText(CartActivity.this, getString(R.string.user_mapping_not_found), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(CartActivity.this, getString(R.string.error_loading_user_mapping), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.user_not_authenticated), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadCartData(String userId) {
        cartConnector.getCartForUser(userId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot cartSnapshot : dataSnapshot.getChildren()) {
                        currentCart = cartSnapshot.getValue(Cart.class);
                        if (currentCart != null) {
                            cartItems = currentCart.getProducts();
                            loadProductsForCartItems();
                            if (currentCart.getPromotionid() != null && !currentCart.getPromotionid().isEmpty()) {
                                loadSelectedPromotion(currentCart.getPromotionid());
                            }
                        }
                    }
                } else {
                    Toast.makeText(CartActivity.this, getString(R.string.cart_not_found), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CartActivity.this, getString(R.string.error_loading_cart), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProductsForCartItems() {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");
        products.clear();

        for (CartItem item : cartItems) {
            productsRef.child(item.getProductid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        if (product.getImage() == null || product.getImage().isEmpty()) {
                            product.setImage("");
                        }
                        products.add(product);
                        if (products.size() == cartItems.size()) {
                            cartAdapter.updateData(cartItems, products);
                            calculateTotal();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(CartActivity.this, getString(R.string.error_loading_product), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void openVoucherActivity() {
        Intent intent = new Intent(this, VoucherActivity.class);
        // Truyền dữ liệu giỏ hàng qua Intent
        intent.putExtra("cartItems", (Serializable) cartItems);
        intent.putExtra("products", (Serializable) products);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedPromotion = (Promotion) data.getSerializableExtra("selected_promotion");
            if (currentCart != null && selectedPromotion != null) {
                currentCart.setPromotionid(selectedPromotion.getPromotionid());
                cartConnector.applyPromotion(currentCart.getCartid(), selectedPromotion.getPromotionid());
                calculateTotal();
                // Mark promotion as used if needed
                promotionConnector.markPromotionAsUsed(selectedPromotion.getPromotionid(),
                        new PromotionConnector.PromotionMarkListener() {
                            @Override
                            public void onMarkSuccess() {
                                // Promotion marked as used successfully
                            }

                            @Override
                            public void onMarkFailed(String errorMessage) {
                                Toast.makeText(CartActivity.this,
                                        "Failed to mark promotion as used: " + errorMessage,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    private void deleteSelectedItems() {
        List<CartItem> itemsToRemove = new ArrayList<>();
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                itemsToRemove.add(item);
            }
        }

        if (itemsToRemove.isEmpty()) {
            Toast.makeText(this, getString(R.string.no_items_selected), Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_delete))
                .setMessage(getString(R.string.confirm_delete_items))
                .setPositiveButton(getString(R.string.delete), (dialog, which) -> {
                    cartItems.removeAll(itemsToRemove);
                    currentCart.setProducts(cartItems);
                    cartConnector.updateCartItem(currentCart.getCartid(), cartItems);
                    calculateTotal();
                    cartAdapter.notifyDataSetChanged();
                    Toast.makeText(this, getString(R.string.items_deleted), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private void checkoutCart() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, getString(R.string.cart_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putExtra("cart", currentCart);
        intent.putExtra("promotion", selectedPromotion);
        startActivity(intent);
    }

    private void calculateTotal() {
        totalAmount = 0;

        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).isSelected()) {
                totalAmount += products.get(i).getPrice() * cartItems.get(i).getQuantity();
            }
        }

        double discount = 0;
        if (selectedPromotion != null) {
            if (selectedPromotion.getDiscounttype().equals("percentage")) {
                discount = totalAmount * (selectedPromotion.getDiscountvalue() / 100);
            } else if (selectedPromotion.getDiscounttype().equals("fixed")) {
                discount = selectedPromotion.getDiscountvalue();
            }
            totalAmount -= discount;

            if (totalAmount < 0) totalAmount = 0;
        }

        currentCart.setTotalamount(totalAmount);
        cartConnector.updateCartTotal(currentCart.getCartid(), totalAmount);

        txtTotalValue.setText(getString(R.string.total_with_discount, totalAmount, discount));
    }

    @Override
    public void onQuantityChanged(int position, int newQuantity) {
        cartItems.get(position).setQuantity(newQuantity);
        cartConnector.updateCartItem(currentCart.getCartid(), cartItems);
        calculateTotal();
    }

    @Override
    public void onItemSelected(int position, boolean isSelected) {
        cartItems.get(position).setSelected(isSelected);
        calculateTotal();
    }



    private void loadSelectedPromotion(String promotionId) {
        promotionConnector.getPromotionById(promotionId, new PromotionConnector.PromotionLoadListener() {
            @Override
            public void onPromotionsLoaded(List<Promotion> loadedPromotions) {
                if (loadedPromotions != null && !loadedPromotions.isEmpty()) {
                    selectedPromotion = loadedPromotions.get(0);
                    calculateTotal();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(CartActivity.this, getString(R.string.error_loading_promotion), Toast.LENGTH_SHORT).show();
            }
        });
    }

}