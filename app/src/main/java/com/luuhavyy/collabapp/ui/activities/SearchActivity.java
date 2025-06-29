package com.luuhavyy.collabapp.ui.activities;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.ui.adapters.ProductAdapter;
import com.luuhavyy.collabapp.ui.dialogs.AddToCartDialogFragment;
import com.luuhavyy.collabapp.ui.viewmodels.ProductViewModel;
import com.luuhavyy.collabapp.utils.LoadingHandlerUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private EditText etSearch;
    private ImageView ivSearch;
    private RecyclerView recyclerProducts;
    private LinearLayout layoutHistory;
    private ProductViewModel productViewModel;
    private ProductAdapter productAdapter;
    private LinearLayout layoutNoProduct;
    private List<String> searchHistory = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupToolbar();

        etSearch = findViewById(R.id.et_search);
        ivSearch = findViewById(R.id.iv_search);
        recyclerProducts = findViewById(R.id.recycler_products);
        layoutHistory = findViewById(R.id.layout_history);
        layoutNoProduct = findViewById(R.id.layout_no_product);

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        productAdapter = new ProductAdapter(this, new ArrayList<>(), productId -> {
            AddToCartDialogFragment dialog = AddToCartDialogFragment.newInstance(productId);
            dialog.show(getSupportFragmentManager(), "AddToCartDialog");
        });
        recyclerProducts.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerProducts.setAdapter(productAdapter);

        // get search history
        loadSearchHistory();
        showSearchHistory();

        ivSearch.setOnClickListener(v -> search());
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search();
                return true;
            }
            return false;
        });

        productViewModel.getProductsLiveData().observe(this, products -> {
            if (products == null || products.isEmpty()) {
                recyclerProducts.setVisibility(View.GONE);
                layoutNoProduct.setVisibility(View.VISIBLE);
            } else {
                recyclerProducts.setVisibility(View.VISIBLE);
                layoutNoProduct.setVisibility(View.GONE);
                productAdapter.setProducts(products);
            }
        });
    }

    private void search() {
        String keyword = etSearch.getText().toString().trim();
        if (keyword.isEmpty()) return;

        // Save search history
        saveSearchHistory(keyword);

        // get search history
        LoadingHandlerUtil.executeOnceWithLoading(this, onLoaded -> {
            productViewModel.searchProductByName(keyword, onLoaded);
        });
        showSearchHistory();
    }

    // Show search history
    private void showSearchHistory() {
        layoutHistory.removeAllViews();
        for (String item : searchHistory) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_search_history, layoutHistory, false);
            TextView tvHistory = view.findViewById(R.id.tv_history);
            ImageView ivRemove = view.findViewById(R.id.iv_remove);
            tvHistory.setText(item);
            tvHistory.setOnClickListener(v -> {
                etSearch.setText(item);
                etSearch.setSelection(item.length());
                search();
            });
            ivRemove.setOnClickListener(v -> {
                removeSearchHistory(item);
                showSearchHistory();
            });
            layoutHistory.addView(view);
        }
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

    // Handle save/remove search history
    private void loadSearchHistory() {
        searchHistory.clear();
        searchHistory.addAll(PreferenceManager.getDefaultSharedPreferences(this)
                .getStringSet("search_history", new HashSet<>()));
    }

    private void saveSearchHistory(String keyword) {
        loadSearchHistory();
        searchHistory.remove(keyword);
        searchHistory.add(0, keyword);
        if (searchHistory.size() > 10) searchHistory = searchHistory.subList(0, 10);
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putStringSet("search_history", new HashSet<>(searchHistory))
                .apply();
    }

    private void removeSearchHistory(String keyword) {
        loadSearchHistory();
        searchHistory.remove(keyword);
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putStringSet("search_history", new HashSet<>(searchHistory))
                .apply();
    }
}
