package com.luuhavyy.collabapp.ui.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.ui.viewmodels.PromotionViewModel;

public class PromotionActivity extends AppCompatActivity {
    private PromotionViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion);

        setupToolbar();

        // Lấy promotionId từ Intent
        String promotionId = getIntent().getStringExtra("promotion_id");
        viewModel = new ViewModelProvider(this).get(PromotionViewModel.class);

        viewModel.getPromotion().observe(this, promotion -> {
            if (promotion != null) {
                ((TextView)findViewById(R.id.tv_promotion_id)).setText("Promotion ID: #" + promotion.getPromotionid());
                ((TextView)findViewById(R.id.tv_discount_type)).setText("Discount Type: " + promotion.getDiscounttype());
                ((TextView)findViewById(R.id.tv_discount_value)).setText("Discount Value: " +
                        (promotion.getDiscounttype().equalsIgnoreCase("percentage") ?
                                promotion.getDiscountvalue() + "%" : promotion.getDiscountvalue() + "đ"));
                ((TextView)findViewById(R.id.tv_promotion_code)).setText("Code: " + promotion.getPromotioncode());
                ((TextView)findViewById(R.id.tv_valid_from)).setText("Valid From: " + promotion.getValidfrom().substring(0,10));
                ((TextView)findViewById(R.id.tv_valid_until)).setText("Valid until: " + promotion.getValiduntil().substring(0,10));
                ((TextView)findViewById(R.id.tv_category)).setText(promotion.getCategoryid());
            }
        });

        viewModel.loadPromotionById(promotionId);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
