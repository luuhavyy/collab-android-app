package com.luuhavyy.collabapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.data.model.Product;
import com.luuhavyy.collabapp.data.model.ReviewWithUser;
import com.luuhavyy.collabapp.ui.adapters.ProductAdapter;
import com.luuhavyy.collabapp.ui.adapters.ReviewAdapter;
import com.luuhavyy.collabapp.ui.dialogs.AddToCartDialogFragment;
import com.luuhavyy.collabapp.ui.viewmodels.ProductViewModel;
import com.luuhavyy.collabapp.utils.AuthUtil;
import com.luuhavyy.collabapp.utils.ImageUtil;
import com.luuhavyy.collabapp.utils.LoadingHandlerUtil;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {
    private ImageView imgProduct;
    private TextView tvName, tvPrice, tvStock, tvDesc, tvReviewScore, tvReviewCount, tvReviewTitle;
    private RecyclerView recyclerReviews;
    private Button btnBuyNow, btnAddToCart;
    private ProductViewModel productViewModel;
    private String productId;
    private ReviewAdapter reviewAdapter;
    private ProductAdapter relatedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        setupToolbar();

        // get productId from Intent
        productId = getIntent().getStringExtra("product_id");
        if (productId == null) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ view
        imgProduct = findViewById(R.id.img_product);
        tvName = findViewById(R.id.tv_product_name);
        tvPrice = findViewById(R.id.tv_product_price);
        tvStock = findViewById(R.id.tv_product_stock);
        tvDesc = findViewById(R.id.tv_product_desc);
        tvReviewScore = findViewById(R.id.tv_review_score);
        tvReviewTitle = findViewById(R.id.tv_review_title);
        recyclerReviews = findViewById(R.id.recycler_reviews);
        btnBuyNow = findViewById(R.id.btn_buy_now);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);

        // ViewModel
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        // show loading
        // get product detail
        LoadingHandlerUtil.executeOnceWithLoading(this, onLoaded -> {
            productViewModel.fetchProductDetail(productId, onLoaded);
        });

        // show product detail
        productViewModel.getProductLiveData().observe(this, product -> {
            if (product == null) {
                Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            // Load ảnh
            imgProduct.setImageBitmap(ImageUtil.decodeBase64ToBitmap(product.getImage()));
            tvName.setText(product.getName());
            tvPrice.setText("VND. " + product.getPrice());
            tvStock.setText("Storage: " + product.getStock());
            tvDesc.setText(product.getDescription());
        });

        // Reviews
        reviewAdapter = new ReviewAdapter();
        recyclerReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerReviews.setAdapter(reviewAdapter);

        productViewModel.fetchReviewsWithUser(productId);

        productViewModel.getReviewWithUserLiveData().observe(this, reviewWithUserList -> {
            reviewAdapter.setData(reviewWithUserList);
            if (reviewWithUserList != null && !reviewWithUserList.isEmpty()) {
                float avg = 0;
                for (ReviewWithUser rw : reviewWithUserList) avg += rw.review.getRating();
                avg = avg / reviewWithUserList.size();
                tvReviewScore.setText(String.format("%.1f", avg));
                tvReviewTitle.setText("Review (" + reviewWithUserList.size() + ")");
            } else {
                tvReviewScore.setText("0.0");
                tvReviewTitle.setText("Review (0)");
            }
        });

        // Products
        RecyclerView recyclerRelated = findViewById(R.id.recycler_related);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerRelated.setLayoutManager(gridLayoutManager);
        recyclerRelated.setAdapter(relatedAdapter);

        // create adapter
        relatedAdapter = new ProductAdapter(this, new ArrayList<>(), productId -> {
            AddToCartDialogFragment dialog = AddToCartDialogFragment.newInstance(productId);
            dialog.show(getSupportFragmentManager(), "AddToCartDialog");
        });

        // set adapter to RecyclerView
        recyclerRelated.setAdapter(relatedAdapter);

        productViewModel.listenToProductsRealtime(() -> {});
        productViewModel.getProductsLiveData().observe(this, products -> {
            List<Product> filtered = new ArrayList<>();
            for (Product p : products) {
                if (!p.getProductid().equals(productId)) {
                    filtered.add(p);
                }
            }
            relatedAdapter.setProducts(filtered);
        });

        btnBuyNow.setOnClickListener(v -> {
            Toast.makeText(this, "Mua ngay sản phẩm!", Toast.LENGTH_SHORT).show();
        });
        btnAddToCart.setOnClickListener(v -> {
            AddToCartDialogFragment dialog = AddToCartDialogFragment.newInstance(productId);
            dialog.show(getSupportFragmentManager(), "AddToCartDialog");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_cart) {
            AuthUtil.checkLoginAndRedirect(this, () -> {
                startActivity(new Intent(this, CartActivity.class));
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product_detail, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
