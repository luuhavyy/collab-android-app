package com.luuhavyy.collabapp.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.data.model.Order;
import com.luuhavyy.collabapp.data.model.Product;
import com.luuhavyy.collabapp.ui.viewmodels.OrderDetailViewModel;
import com.luuhavyy.collabapp.utils.ImageUtil;

import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {
    private OrderDetailViewModel viewModel;
    // View binding hoặc findViewById
    private TextView tvStatus, tvOrderId, tvUserName, tvUserEmail, tvUserAddress, tvUserPhone, tvShippingDate, tvShippingAddress, tvPaymentMethod, tvTotalPaid;
    private LinearLayout layoutItems;
    private Button btnMoreProducts, btnBackHome;
    private ActivityResultLauncher<Intent> reviewLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        setupToolbar();




        // 1. Init View
        tvStatus = findViewById(R.id.tv_status);
        tvOrderId = findViewById(R.id.tv_order_id);
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserEmail = findViewById(R.id.tv_user_email);
        tvUserAddress = findViewById(R.id.tv_user_address);
        tvUserPhone = findViewById(R.id.tv_user_phone);
        tvShippingDate = findViewById(R.id.tv_shipping_date);
        tvShippingAddress = findViewById(R.id.tv_shipping_address);
        tvPaymentMethod = findViewById(R.id.tv_payment_method);
        tvTotalPaid = findViewById(R.id.tv_total_paid);
        layoutItems = findViewById(R.id.layout_items);
        btnMoreProducts = findViewById(R.id.btn_more_products);
        btnBackHome = findViewById(R.id.btn_back_home);

        // 2. Lấy orderId từ Intent
        String orderId = getIntent().getStringExtra("order_id");
        if (orderId == null) {
            Toast.makeText(this, "Order not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        reviewLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Gọi lại để load order mới (ẩn nút review)
                        viewModel.fetchOrderById(orderId);
                    }
                }
        );

        // 3. Init ViewModel và observe
        viewModel = new ViewModelProvider(this).get(OrderDetailViewModel.class);

        // Observe Order info
        viewModel.getOrderLiveData().observe(this, order -> {
            if (order == null) return;

            tvStatus.setText(order.getStatus());
            tvOrderId.setText("Order ID: #" + order.getOrderid());
            tvUserName.setText("User: " + order.getUserid());
            tvUserEmail.setText(""); // Thêm nếu có
            tvUserAddress.setText(order.getShippingdetails().getAddress());
            tvUserPhone.setText(order.getShippingdetails().getPhonenumber());
            tvShippingDate.setText("Expected Shipping Date: " + order.getShippingdetails().getEstimatedDelivery());
            tvShippingAddress.setText(order.getShippingdetails().getAddress());
            tvPaymentMethod.setText(order.getPaymentmethod());
            tvTotalPaid.setText((int) order.getTotalafterpromo() + "đ");
        });

        // Observe danh sách Product theo thứ tự các ProductItem trong order
        viewModel.getProductListLiveData().observe(this, products -> {
            layoutItems.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(this);

            Order order = viewModel.getOrderLiveData().getValue();
            if (order == null || products == null) return;

            List<Order.ProductItem> productItems = order.getProducts();
            for (int i = 0; i < productItems.size(); i++) {
                Order.ProductItem item = productItems.get(i);
                Product product = i < products.size() ? products.get(i) : null;

                View itemView = inflater.inflate(R.layout.item_order_product, layoutItems, false);
                ImageView imgProduct = itemView.findViewById(R.id.img_product);
                TextView tvName = itemView.findViewById(R.id.tv_name);
                TextView tvPrice = itemView.findViewById(R.id.tv_price);
                TextView tvQuantity = itemView.findViewById(R.id.tv_quantity);
                Button btnReview = itemView.findViewById(R.id.btn_review);

                tvQuantity.setText("x" + item.getQuantity());
                tvPrice.setText("VND. " + (int) item.getPrice());

                if (product != null) {
                    tvName.setText(product.getName());
                    Bitmap bitmap = ImageUtil.decodeBase64ToBitmap(product.getImage());
                    if (bitmap != null) {
                        imgProduct.setImageBitmap(bitmap);
                    } else {
                        imgProduct.setImageResource(R.drawable.sample_product);
                    }
                } else {
                    tvName.setText(item.getProductid());
                    imgProduct.setImageResource(R.drawable.sample_product);
                }

                if ("Delivered".equalsIgnoreCase(order.getStatus()) && item.isAllowreview()) {
                    btnReview.setVisibility(View.VISIBLE);
                    btnReview.setOnClickListener(v -> {
                        // Chuyển sang màn hình ReviewProductActivity, truyền orderid, productid, v.v.
                        Intent intent = new Intent(this, ReviewProductActivity.class);
                        intent.putExtra("order_id", order.getOrderid());
                        intent.putExtra("product_id", item.getProductid());
                        // Thêm các extra khác nếu cần
                        reviewLauncher.launch(intent);
                    });
                } else {
                    btnReview.setVisibility(View.GONE);
                }

                layoutItems.addView(itemView);
            }
        });

        // 4. Trigger fetch dữ liệu
        viewModel.fetchOrderById(orderId);

        btnBackHome.setOnClickListener(v -> finish());
        btnMoreProducts.setOnClickListener(v -> {
            Intent intent = new Intent(OrderDetailActivity.this, CartActivity.class); // Hoặc ProductListActivity
            startActivity(intent);
        });
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }
}
