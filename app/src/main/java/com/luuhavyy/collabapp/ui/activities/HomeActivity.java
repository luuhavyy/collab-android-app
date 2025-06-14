package com.luuhavyy.collabapp.ui.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.utils.AuthUtil;

public class HomeActivity extends AppCompatActivity {
    private NavController navController;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment == null) {
            finish();
            return;
        }

        navController = navHostFragment.getNavController();
        bottomNav = findViewById(R.id.bottom_nav);

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                navController.navigate(R.id.nav_home);
                return true;
            } else if (itemId == R.id.nav_noti) {
                navController.navigate(R.id.nav_noti);
                return true;
            } else if (itemId == R.id.nav_order) {
                navController.navigate(R.id.nav_order);
                return true;
            } else if (itemId == R.id.nav_profile) {
                AuthUtil.checkLoginAndRedirect(this,
                        () -> navController.navigate(R.id.nav_profile));
                return AuthUtil.isLoggedIn();
            }

            return false;
        });
    }
}