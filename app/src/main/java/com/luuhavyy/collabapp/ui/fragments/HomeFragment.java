package com.luuhavyy.collabapp.ui.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

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
import com.luuhavyy.collabapp.ui.activities.CartActivity;
import com.luuhavyy.collabapp.ui.adapters.BannerAdapter;
import com.luuhavyy.collabapp.ui.adapters.ProductAdapter;
import com.luuhavyy.collabapp.ui.dialogs.DialogFilterAndSorting;
import com.luuhavyy.collabapp.ui.viewmodels.ProductViewModel;
import com.luuhavyy.collabapp.utils.AuthUtil;
import com.luuhavyy.collabapp.utils.LoadingHandlerUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private ProductViewModel productViewModel;
    private RecyclerView recyclerView;

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

        // Setup ViewModel
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        LoadingHandlerUtil.executeOnceWithLoading(requireContext(), onLoaded -> {
            productViewModel.listenToProductsRealtime(onLoaded); // start listening for changes
        });

        productViewModel.getProductsLiveData().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                recyclerView.setAdapter(new ProductAdapter(products));
            } else {
                Toast.makeText(requireContext(), "Error loading products", Toast.LENGTH_SHORT).show();
            }
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