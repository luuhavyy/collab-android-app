package com.luuhavyy.collabapp.ui.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.ui.viewmodels.UserViewModel;

public class UserProfileActivity extends AppCompatActivity {
    private ImageView imgAvatar;
    private TextView txtName, txtEmail;
    private UserViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        imgAvatar = findViewById(R.id.imgAvatar);
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);

        viewModel = new ViewModelProvider(this).get(UserViewModel.class);

        viewModel.getUser().observe(this, user -> {
            if (user != null) {
                txtName.setText(user.getName());
                txtEmail.setText(user.getEmail());
                Glide.with(this).load(user.getProfilepicture()).into(imgAvatar);
            }
        });
    }
}
