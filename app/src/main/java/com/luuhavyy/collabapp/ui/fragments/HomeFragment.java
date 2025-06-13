package com.luuhavyy.collabapp.ui.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.luuhavyy.collabapp.ui.activities.CartActivity;
import com.luuhavyy.collabapp.ui.activities.LoginActivity;
import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.data.model.Banner;
import com.luuhavyy.collabapp.ui.adapters.BannerAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


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

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_cart) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    showLoginDialog();
                } else {
                    startActivity(new Intent(requireContext(), CartActivity.class));
                }
                return true;
            }
            return false;
        });

        return view;
    }

    private void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_login_prompt, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        view.findViewById(R.id.btn_close).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.btn_login).setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(requireContext(), LoginActivity.class));
        });

        dialog.show();
    }
}