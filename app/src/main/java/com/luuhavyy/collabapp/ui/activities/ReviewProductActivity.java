package com.luuhavyy.collabapp.ui.activities;

import android.os.Bundle;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.data.model.Review;
import com.luuhavyy.collabapp.utils.ImageUtil;

public class ReviewProductActivity extends AppCompatActivity {
    private ImageView imgProduct;
    private TextView tvProductName;
    private RatingBar ratingBar;
    private EditText edtReview;
    private Button btnSendReview;

    private String orderId, productId, productName, productImage;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_product);

        // 1. Init view
        imgProduct = findViewById(R.id.img_product);
        tvProductName = findViewById(R.id.tv_product_name);
        ratingBar = findViewById(R.id.rating_bar);
        edtReview = findViewById(R.id.edt_review);
        btnSendReview = findViewById(R.id.btn_send_review);

        // 2. Nhận tham số từ Intent
        orderId = getIntent().getStringExtra("order_id");
        productId = getIntent().getStringExtra("product_id");
        productName = getIntent().getStringExtra("product_name");
        productImage = getIntent().getStringExtra("product_image");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        tvProductName.setText(productName);
        imgProduct.setImageBitmap(ImageUtil.decodeBase64ToBitmap(productImage)); // Nếu ảnh base64

        // 3. Gửi review
        btnSendReview.setOnClickListener(v -> {
            int rating = (int) ratingBar.getRating();
            String reviewText = edtReview.getText().toString().trim();

            if (rating == 0) {
                Toast.makeText(this, "Please select number of stars", Toast.LENGTH_SHORT).show();
                return;
            }
            if (reviewText.isEmpty()) {
                Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show();
                return;
            }

            sendReview(rating, reviewText);
        });
    }

    private void sendReview(int rating, String reviewText) {
        // 1. Tạo review object
        String reviewId = FirebaseDatabase.getInstance().getReference("reviews").push().getKey();
        String timestamp = String.valueOf(System.currentTimeMillis()); // hoặc dùng SimpleDateFormat

        Review review = Review.builder()
                .reviewid(reviewId)
                .orderid(orderId)
                .productid(productId)
                .userid(userId)
                .rating(rating)
                .review(reviewText)
                .timestamp(timestamp)
                .build();

        // 2. Ghi review vào Firebase
        FirebaseDatabase.getInstance().getReference("reviews")
                .child(reviewId)
                .setValue(review)
                .addOnSuccessListener(aVoid -> {
                    // 3. Cập nhật allowreview về false
                    updateAllowReviewInOrder();
                    Toast.makeText(this, "Review sent!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error sending review: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateAllowReviewInOrder() {
        DatabaseReference orderRef = FirebaseDatabase.getInstance()
                .getReference("orders").child(orderId).child("products");
        // Lấy products array, set allowreview về false cho productId đã review
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    String pid = item.child("productid").getValue(String.class);
                    if (productId.equals(pid)) {
                        item.getRef().child("allowreview").setValue(false);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
