package com.luuhavyy.collabapp.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.data.model.Banner;
import com.luuhavyy.collabapp.data.model.ProductFilterSort;
import com.luuhavyy.collabapp.ui.activities.CartActivity;
import com.luuhavyy.collabapp.ui.activities.SearchActivity;
import com.luuhavyy.collabapp.ui.adapters.BannerAdapter;
import com.luuhavyy.collabapp.ui.adapters.ProductAdapter;
import com.luuhavyy.collabapp.ui.dialogs.AddToCartDialogFragment;
import com.luuhavyy.collabapp.ui.dialogs.DialogFilterAndSorting;
import com.luuhavyy.collabapp.ui.viewmodels.ProductViewModel;
import com.luuhavyy.collabapp.utils.AuthUtil;
import com.luuhavyy.collabapp.utils.LoadingHandlerUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private ProductViewModel productViewModel;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ViewPager2 viewPager = view.findViewById(R.id.view_pager_banner);

        List<Banner> banners = new ArrayList<>();
        banners.add(new Banner(R.drawable.sample_glasses_banner, "WELCOME TO\nCOLLAB SHOP", "All glasses you can find"));
        banners.add(new Banner(R.drawable.sample_glasses_banner, "NEW COLLECTION", "Just arrived today"));
        banners.add(new Banner(R.drawable.sample_glasses_banner, "SALE UP TO 50%", "On selected items"));

        BannerAdapter bannerAdapter = new BannerAdapter(banners);
        viewPager.setAdapter(bannerAdapter);

        // Setup RecyclerView for products
        recyclerView = view.findViewById(R.id.recycler_products);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        productAdapter = new ProductAdapter(requireContext(), new ArrayList<>(), productId -> {
            AddToCartDialogFragment dialog = AddToCartDialogFragment.newInstance(productId);
            dialog.show(getChildFragmentManager(), "AddToCartDialog");
        });
        recyclerView.setAdapter(productAdapter);

        // Setup ViewModel
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        LoadingHandlerUtil.executeOnceWithLoading(requireContext(), onLoaded -> {
            productViewModel.listenToProductsRealtime(onLoaded); // start listening for changes
        });

        productViewModel.getProductsLiveData().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                productAdapter.setProducts(products);
            } else {
                Toast.makeText(requireContext(), "Error loading products", Toast.LENGTH_SHORT).show();
            }
        });

        LinearLayout btnSunglasses = view.findViewById(R.id.btn_sunglasses);
        LinearLayout btnFrames= view.findViewById(R.id.btn_frames);


        btnSunglasses.setOnClickListener(v -> {
            ProductFilterSort filter = new ProductFilterSort(
                    0,              // minPrice
                    Float.MAX_VALUE,// maxPrice
                    false,          // frameGlasses
                    true,           // sunglasses
                    null            // sortBy
            );
            LoadingHandlerUtil.executeOnceWithLoading(requireContext(), onLoaded -> {
                productViewModel.fetchProductsWithFilter(filter, onLoaded);
            });
        });

        btnFrames.setOnClickListener(v -> {
            ProductFilterSort filter = new ProductFilterSort(
                    0,
                    Float.MAX_VALUE,
                    true,           // frameGlasses
                    false,          // sunglasses
                    null
            );
            LoadingHandlerUtil.executeOnceWithLoading(requireContext(), onLoaded -> {
                productViewModel.fetchProductsWithFilter(filter, onLoaded);
            });
        });

        // Setup toolbar menu (cart icon)
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_cart) {
                AuthUtil.checkLoginAndRedirect(requireContext(), () -> {
                    startActivity(new Intent(requireContext(), CartActivity.class));
                });
                return true;
            }
            return false;
        });

        // Setup search
        EditText etSearch = view.findViewById(R.id.et_search);
        ImageView ivSearch = view.findViewById(R.id.iv_search);

        View.OnClickListener openSearch = v -> {
            startActivity(new Intent(requireContext(), SearchActivity.class));
        };
        etSearch.setOnClickListener(openSearch);
        ivSearch.setOnClickListener(openSearch);

        // Setup filter & sorting
        TextView tvFilterSort = view.findViewById(R.id.tv_filter_sort);
        tvFilterSort.setOnClickListener(v -> {
            // Create dialog
            DialogFilterAndSorting dialog = DialogFilterAndSorting.newInstance();
            dialog.setOnFilterApplyListener(filterSort -> {
                // Apply filter & sort
                LoadingHandlerUtil.executeOnceWithLoading(requireContext(), onLoaded -> {
                    productViewModel.fetchProductsWithFilter(filterSort, onLoaded);
                });
            });
            dialog.show(getChildFragmentManager(), "filter_sort");
        });

        return view;
    }
}