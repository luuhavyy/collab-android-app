package com.luuhavyy.collabapp.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.connectors.CheckoutConnector;
import com.luuhavyy.collabapp.data.model.Cart;
import com.luuhavyy.collabapp.data.model.CartItem;
import com.luuhavyy.collabapp.data.model.Order;
import com.luuhavyy.collabapp.data.model.Product;
import com.luuhavyy.collabapp.data.model.Promotion;
import com.luuhavyy.collabapp.data.model.User;
import com.luuhavyy.collabapp.ui.adapters.CheckoutAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CheckoutActivity extends AppCompatActivity {
    // Main layout views
    private EditText edtCheckoutReceiverName;
    private EditText edtReceiverEmail;
    private EditText edtReceiverAddress;
    private EditText edtReceiverPhoneNumber;
    private RadioButton radioVisa;
    private RadioButton radioCOD;
    private RadioGroup paymentMethodGroup;
    private ListView listOrderItems;
    private TextView txtSubtotal;
    private TextView txtDiscount;
    private TextView txtDeliveryFee;
    private TextView txtTotal;
    private Button btnContinue;

    // Banking info layout views
    private EditText edtPIN;
    private EditText edtCardName;
    private TextView txtEditPersonalInfor;
    private TextView txtEdit;

    // Notification layout views
    private TextView txtCheckoutStatus;
    private TextView txtTotalValue;
    private Button btnContinueToShop;
    private Button btnHomepage;

    private LinearLayout mainLayout;
    private View bankingInfoLayout;
    private View notificationLayout;

    private CheckoutAdapter checkoutAdapter;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private List<CartItem> selectedCartItems = new ArrayList<>();
    private List<Product> selectedProducts = new ArrayList<>();
    private Cart currentCart;
    private Promotion selectedPromotion;
    private double subtotal = 0;
    private double discount = 0;
    private final double deliveryFee = 20000;
    private String paymentMethod = "";
    private User currentUser;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mainLayout = findViewById(R.id.main);
        bankingInfoLayout = LayoutInflater.from(this).inflate(R.layout.item_checkout_banking_infor, null);
        notificationLayout = LayoutInflater.from(this).inflate(R.layout.item_checkout_notification, null);

        initializeViews();
        setupListeners();
        loadIntentData();
        loadUserData();
    }

    private void initializeViews() {
        // Main layout views
        edtCheckoutReceiverName = findViewById(R.id.edtCheckoutReceiverName);
        edtReceiverEmail = findViewById(R.id.edtReceiverEmail);
        edtReceiverAddress = findViewById(R.id.edtReceiverAddress);
        edtReceiverPhoneNumber = findViewById(R.id.edtReceiverPhoneNumber);
        radioVisa = findViewById(R.id.radioVisa);
        radioCOD = findViewById(R.id.radioCOD);
        paymentMethodGroup = findViewById(R.id.paymentMethodGroup);
        listOrderItems = findViewById(R.id.listOrderItems);
        txtSubtotal = findViewById(R.id.txtSubtotal);
        txtDiscount = findViewById(R.id.txtDiscount);
        txtDeliveryFee = findViewById(R.id.txtDeliveryFee);
        txtTotal = findViewById(R.id.txtTotal);
        btnContinue = findViewById(R.id.btnContinue);

        // Banking info layout views
        edtPIN = bankingInfoLayout.findViewById(R.id.edtPIN);
        edtCardName = bankingInfoLayout.findViewById(R.id.edtCardName);
        txtEditPersonalInfor = bankingInfoLayout.findViewById(R.id.txtEditPersonalInfor);
        txtEdit = bankingInfoLayout.findViewById(R.id.txtEdit);

        // Notification layout views
        txtCheckoutStatus = notificationLayout.findViewById(R.id.txtCheckoutStatus);
        txtTotalValue = notificationLayout.findViewById(R.id.txtTotalValue);
        btnContinueToShop = notificationLayout.findViewById(R.id.btnContinueToShop);
        btnHomepage = notificationLayout.findViewById(R.id.btnHomepage);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        // Re-set payment method if it was previously selected
        if (paymentMethod != null) {
            if (paymentMethod.equals("VISA")) {
                radioVisa.setChecked(true);
                radioVisa.setTextColor(Color.parseColor("#129D12"));
                radioCOD.setTextColor(Color.BLACK);
            } else if (paymentMethod.equals("COD")) {
                radioCOD.setChecked(true);
                radioCOD.setTextColor(Color.parseColor("#129D12"));
                radioVisa.setTextColor(Color.BLACK);
            }
        }

        // Restore the adapter if we have data
        if (checkoutAdapter == null && !selectedCartItems.isEmpty() && !selectedProducts.isEmpty()) {
            checkoutAdapter = new CheckoutAdapter(this, selectedCartItems, selectedProducts);
            listOrderItems.setAdapter(checkoutAdapter);
        }
    }

    private void setupListeners() {
        // Text change listeners for form validation
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateForm();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        edtCheckoutReceiverName.addTextChangedListener(textWatcher);
        edtReceiverEmail.addTextChangedListener(textWatcher);
        edtReceiverAddress.addTextChangedListener(textWatcher);
        edtReceiverPhoneNumber.addTextChangedListener(textWatcher);

        // Payment method selection
        paymentMethodGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioVisa) {
                radioVisa.setTextColor(Color.parseColor("#129D12"));
                radioCOD.setTextColor(Color.BLACK);
                paymentMethod = "VISA";
            } else if (checkedId == R.id.radioCOD) {
                radioCOD.setTextColor(Color.parseColor("#129D12"));
                radioVisa.setTextColor(Color.BLACK);
                paymentMethod = "COD";
            }
            validateForm();
        });

        // Continue button click - Switch to banking info layout or show confirmation for COD
        btnContinue.setOnClickListener(v -> {
            if (validateForm()) {
                if (paymentMethod.equals("COD")) {
                    showConfirmationDialog();
                } else {
                    switchToBankingInfoLayout();
                }
            }
        });
        // Banking info layout listeners
        // Banking info layout listeners
        txtEditPersonalInfor.setOnClickListener(v -> {
            switchToMainLayout();
            validateForm(); // Ensure form validation is triggered
        });

        txtEdit.setOnClickListener(v -> {
            switchToMainLayout();
            validateForm(); // Ensure form validation is triggered
        });


        /// Banking info continue button
        bankingInfoLayout.findViewById(R.id.btnContinue).setOnClickListener(v -> {
            if (validateBankingInfoForm()) {
                showConfirmationDialog();
            }
        });

        // Notification layout buttons
        btnContinueToShop.setOnClickListener(v -> {
            // Continue shopping logic
            finish();
        });

        btnHomepage.setOnClickListener(v -> {
            Intent intent=new Intent(CheckoutActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        // Add text watchers for banking info fields
        edtPIN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateBankingInfoForm();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        edtCardName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateBankingInfoForm();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void switchToMainLayout() {
        mainLayout.removeAllViews();
        View mainView = LayoutInflater.from(this).inflate(R.layout.activity_checkout, mainLayout, false);
        mainLayout.addView(mainView);

        // Reinitialize views with the new layout
        initializeViews();

        // Restore the adapter and data
        if (checkoutAdapter == null && !selectedCartItems.isEmpty()) {
            checkoutAdapter = new CheckoutAdapter(this, selectedCartItems, selectedProducts);
        }

        if (listOrderItems != null && checkoutAdapter != null) {
            listOrderItems.setAdapter(checkoutAdapter);
        }

        populateUserInformation();
        calculateOrderSummary();
        setupListeners();
    }

    private void switchToBankingInfoLayout() {
        mainLayout.removeAllViews();
        mainLayout.addView(bankingInfoLayout);

        // Set personal information
        TextView txtReceiverName = bankingInfoLayout.findViewById(R.id.txtCheckoutReceiverName);
        TextView txtReceiverEmailView = bankingInfoLayout.findViewById(R.id.txtReceiverEmail);
        TextView txtReceiverAddressView = bankingInfoLayout.findViewById(R.id.txtReceiverAddress);
        TextView txtReceiverPhoneNumberView = bankingInfoLayout.findViewById(R.id.txtReceiverPhoneNumber);
        TextView txtPaymentMethodView = bankingInfoLayout.findViewById(R.id.txtPaymentMethod);

        txtReceiverName.setText(edtCheckoutReceiverName.getText().toString());
        txtReceiverEmailView.setText(edtReceiverEmail.getText().toString());
        txtReceiverAddressView.setText(edtReceiverAddress.getText().toString());
        txtReceiverPhoneNumberView.setText(edtReceiverPhoneNumber.getText().toString());
        txtPaymentMethodView.setText(paymentMethod);

        // Set order items and summary - Use the existing adapter
        ListView listOrderItemsBanking = bankingInfoLayout.findViewById(R.id.listOrderItems);
        listOrderItemsBanking.setAdapter(checkoutAdapter);

        TextView txtSubtotalBanking = bankingInfoLayout.findViewById(R.id.txtSubtotal);
        TextView txtDiscountBanking = bankingInfoLayout.findViewById(R.id.txtDiscount);
        TextView txtDeliveryFeeBanking = bankingInfoLayout.findViewById(R.id.txtDeliveryFee);
        TextView txtTotalBanking = bankingInfoLayout.findViewById(R.id.txtTotal);

        txtSubtotalBanking.setText(String.format("%,.0f VNĐ", subtotal));
        txtDiscountBanking.setText(String.format("%,.0f VNĐ", discount));
        txtDeliveryFeeBanking.setText(String.format("%,.0f VNĐ", deliveryFee));
        txtTotalBanking.setText(String.format("%,.0f VNĐ", subtotal - discount + deliveryFee));

        // Set click listeners for edit buttons
        txtEditPersonalInfor.setOnClickListener(v -> switchToMainLayout());
        txtEdit.setOnClickListener(v -> switchToMainLayout());
    }

    private void switchToNotificationLayout() {
        mainLayout.removeAllViews();
        mainLayout.addView(notificationLayout);

        // Set personal information
        TextView txtReceiverName = notificationLayout.findViewById(R.id.txtCheckoutReceiverName);
        TextView txtReceiverEmailView = notificationLayout.findViewById(R.id.txtReceiverEmail);
        TextView txtReceiverAddressView = notificationLayout.findViewById(R.id.txtReceiverAddress);
        TextView txtReceiverPhoneNumberView = notificationLayout.findViewById(R.id.txtReceiverPhoneNumber);
        TextView txtPaymentMethodView = notificationLayout.findViewById(R.id.txtPaymentMethod);

        txtReceiverName.setText(edtCheckoutReceiverName.getText().toString());
        txtReceiverEmailView.setText(edtReceiverEmail.getText().toString());
        txtReceiverAddressView.setText(edtReceiverAddress.getText().toString());
        txtReceiverPhoneNumberView.setText(edtReceiverPhoneNumber.getText().toString());
        txtPaymentMethodView.setText(paymentMethod);

        // Set order items
        ListView listOrderItemsNotification = notificationLayout.findViewById(R.id.listOrderItems);
        checkoutAdapter = new CheckoutAdapter(this, selectedCartItems, selectedProducts);
        listOrderItemsNotification.setAdapter(checkoutAdapter);

        // Set total value
        txtTotalValue.setText(String.format("%,.0f VNĐ", subtotal - discount + deliveryFee));
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Checkout")
                .setMessage("Are you sure you want to complete this order?")
                .setPositiveButton("YES", (dialog, which) -> {
                    createOrder();
                    switchToNotificationLayout();
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }

    private void createOrder() {
        CheckoutConnector connector = new CheckoutConnector();

        // Generate sequential order ID by checking the last order in database
        databaseReference.child("orders").orderByKey().limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String lastOrderId = "order000"; // Default starting ID
                        if (snapshot.exists()) {
                            for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                                lastOrderId = orderSnapshot.getKey();
                            }
                        }

                        // Extract number and increment
                        int lastNumber = Integer.parseInt(lastOrderId.replace("order", ""));
                        String orderId = String.format("order%03d", lastNumber + 1);

                        // Create shipping details
                        Order.ShippingDetails shippingDetails = new Order.ShippingDetails(
                                edtReceiverAddress.getText().toString(),
                                edtReceiverPhoneNumber.getText().toString(),
                                "3-5 business days" // Estimated delivery
                        );

                        // Create product items
                        List<Order.ProductItem> productItems = new ArrayList<>();
                        for (int i = 0; i < selectedCartItems.size(); i++) {
                            CartItem cartItem = selectedCartItems.get(i);
                            Product product = selectedProducts.get(i);
                            productItems.add(new Order.ProductItem(
                                    cartItem.getProductid(),
                                    cartItem.getQuantity(),
                                    product.getPrice(),
                                    true
                            ));
                        }

                        // Get current date
                        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                        // Create order
                        Order order = new Order(
                                orderId,
                                userId,
                                productItems,
                                "Pending",
                                subtotal,
                                discount,
                                subtotal - discount + deliveryFee,
                                currentDate,
                                shippingDetails,
                                paymentMethod
                        );

                        // Save order to database
                        connector.createOrder(order);

                        // Show success message
                        txtCheckoutStatus.setText("Order placed successfully!");
                        switchToNotificationLayout();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CheckoutActivity.this, "Error generating order ID", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateForm() {
        boolean isValid = !edtCheckoutReceiverName.getText().toString().isEmpty() &&
                !edtReceiverEmail.getText().toString().isEmpty() &&
                !edtReceiverAddress.getText().toString().isEmpty() &&
                !edtReceiverPhoneNumber.getText().toString().isEmpty() &&
                paymentMethodGroup.getCheckedRadioButtonId() != -1;

        if (isValid) {
            btnContinue.setBackgroundColor(Color.parseColor("#063B06"));
            btnContinue.setEnabled(true);
        } else {
            btnContinue.setBackgroundColor(Color.parseColor("#B6B0B0"));
            btnContinue.setEnabled(false);
        }

        return isValid;
    }

    private boolean validateBankingInfoForm() {
        boolean isValid = true;
        Button btnBankingContinue = bankingInfoLayout.findViewById(R.id.btnContinue);

        if (paymentMethod.equals("VISA")) {
            isValid = !edtPIN.getText().toString().isEmpty() &&
                    !edtCardName.getText().toString().isEmpty();
        }

        if (isValid) {
            btnBankingContinue.setBackgroundColor(Color.parseColor("#063B06"));
            btnBankingContinue.setEnabled(true);
        } else {
            btnBankingContinue.setBackgroundColor(Color.parseColor("#B6B0B0"));
            btnBankingContinue.setEnabled(false);
        }

        return isValid;
    }

    private void loadIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            currentCart = (Cart) intent.getSerializableExtra("cart");
            selectedPromotion = (Promotion) intent.getSerializableExtra("promotion");

            if (currentCart != null) {
                // Get only selected items
                for (CartItem item : currentCart.getProducts()) {
                    if (item.isSelected()) {
                        selectedCartItems.add(item);
                    }
                }

                // Load products for selected items
                loadProductsForSelectedItems();
            }
        }
    }

    private void loadProductsForSelectedItems() {
        DatabaseReference productsRef = databaseReference.child("products");
        for (CartItem item : selectedCartItems) {
            productsRef.child(item.getProductid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        selectedProducts.add(product);
                        if (selectedProducts.size() == selectedCartItems.size()) {
                            setupOrderItemsList();
                            calculateOrderSummary();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(CheckoutActivity.this, "Error loading product information", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadUserData() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            databaseReference.child("firebaseUidToUserId").child(firebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                userId = snapshot.getValue(String.class);
                                databaseReference.child("users").child(userId)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                currentUser = snapshot.getValue(User.class);
                                                if (currentUser != null) {
                                                    populateUserInformation();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(CheckoutActivity.this, "Error loading user information", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(CheckoutActivity.this, "Error loading user mapping", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void populateUserInformation() {
        edtCheckoutReceiverName.setText(currentUser.getName());
        edtReceiverEmail.setText(currentUser.getEmail());
        edtReceiverPhoneNumber.setText(currentUser.getPhonenumber());

        if (currentUser.getDefaultaddress() != null) {
            User.Address address = currentUser.getDefaultaddress();
            String fullAddress = address.getStreet() + ", " + address.getCity() + ", " + address.getCountry();
            edtReceiverAddress.setText(fullAddress);
        }
    }

//    private void setupOrderItemsList() {
//        checkoutAdapter = new CheckoutAdapter(this, selectedCartItems, selectedProducts);
//        listOrderItems.setAdapter(checkoutAdapter);
//    }
    private void setupOrderItemsList() {
        if (selectedCartItems != null && selectedProducts != null &&
                selectedCartItems.size() == selectedProducts.size()) {
            checkoutAdapter = new CheckoutAdapter(this, selectedCartItems, selectedProducts);
            listOrderItems.setAdapter(checkoutAdapter);
        }
    }
    private void calculateOrderSummary() {
        // Calculate subtotal
        subtotal = 0;
        for (int i = 0; i < selectedCartItems.size(); i++) {
            CartItem item = selectedCartItems.get(i);
            Product product = selectedProducts.get(i);
            subtotal += product.getPrice() * item.getQuantity();
        }

        // Calculate discount
        if (selectedPromotion != null) {
            if ("percentage".equals(selectedPromotion.getDiscounttype())) {
                discount = subtotal * ((double) selectedPromotion.getDiscountvalue() / 100.0);
            } else if ("fixed".equals(selectedPromotion.getDiscounttype())) {
                discount = selectedPromotion.getDiscountvalue();
            }
            discount = Math.min(discount, subtotal);
        } else {
            discount = 0;
        }

        // Update UI
        txtSubtotal.setText(String.format("%,.0f VNĐ", subtotal));
        txtDiscount.setText(String.format("%,.0f VNĐ", discount));
        txtDeliveryFee.setText(String.format("%,.0f VNĐ", deliveryFee));
        txtTotal.setText(String.format("%,.0f VNĐ", subtotal - discount + deliveryFee));
    }

    public void do_back_cart(View view) {
        Intent intent = new Intent(CheckoutActivity.this, CartActivity.class);
        startActivity(intent);
    }
}