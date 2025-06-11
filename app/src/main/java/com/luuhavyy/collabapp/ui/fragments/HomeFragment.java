package com.luuhavyy.collabapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.data.model.Banner;
import com.luuhavyy.collabapp.ui.adapters.BannerAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

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

        return view;
    }
}