package com.luuhavyy.collabapp.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.data.model.User;
import com.luuhavyy.collabapp.ui.viewmodels.UserViewModel;
import com.luuhavyy.collabapp.utils.LoadingHandlerUtil;

public class EditInformationActivity extends AppCompatActivity {
    private User currentUser;
    private final UserViewModel userViewModel = new UserViewModel();
    private final String userId = "user005";
    private EditText etName, etEmail, etAddress, etPhone;
    private Spinner spinnerGender;
    private Button btnSave;
    private final String[] genderOptions = {"Male", "Female", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_information);
        setupToolbar();
        initViews();
        loadUserData();

        btnSave.setOnClickListener(v -> {
            if (currentUser == null) return;
            updateUserFromForm(currentUser);

            LoadingHandlerUtil.executeWithLoading(this, callback ->
                    userViewModel.updateUserInfo(userId, currentUser,
                            () -> {
                                Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                                callback.onComplete();
                                finish();
                            },
                            () -> {
                                Toast.makeText(this, "Failed to save!", Toast.LENGTH_SHORT).show();
                                callback.onComplete();
                            })
            );
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Information");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
    }

    private void initViews() {
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etAddress = findViewById(R.id.et_address);
        etPhone = findViewById(R.id.et_phone);
        spinnerGender = findViewById(R.id.spinner_gender);
        btnSave = findViewById(R.id.btn_save);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, genderOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);
    }

    private void loadUserData() {
        LoadingHandlerUtil.executeOnceWithLoading(this, callback -> {
            userViewModel.listenToUserRealtime(userId, () -> {
                currentUser = userViewModel.getUserLiveData().getValue();
                if (currentUser != null) populateUserToForm(currentUser);
                else Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                callback.onComplete();
            });
        });
    }

    private void populateUserToForm(User user) {
        etName.setText(user.getName());
        etEmail.setText(user.getEmail());
        etAddress.setText(user.getAddress());
        etPhone.setText(user.getPhonenumber());

        for (int i = 0; i < genderOptions.length; i++) {
            if (genderOptions[i].equalsIgnoreCase(user.getGender())) {
                spinnerGender.setSelection(i);
                break;
            }
        }
    }

    private void updateUserFromForm(User user) {
        user.setName(etName.getText().toString());
        user.setEmail(etEmail.getText().toString());
        user.setAddress(etAddress.getText().toString());
        user.setPhonenumber(etPhone.getText().toString());
        user.setGender(spinnerGender.getSelectedItem().toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
