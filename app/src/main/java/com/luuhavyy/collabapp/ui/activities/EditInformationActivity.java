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
import com.luuhavyy.collabapp.data.service.FirebaseImageService;

public class EditInformationActivity extends AppCompatActivity {

    private EditText etName, etEmail, etAddress, etPhone, etGender;
    private Spinner spinnerGender;
    private Button btnSave;
    FirebaseImageService imageService = new FirebaseImageService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_information);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Information");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etAddress = findViewById(R.id.et_address);
        etPhone = findViewById(R.id.et_phone);
        spinnerGender = findViewById(R.id.spinner_gender);
        btnSave = findViewById(R.id.btn_save);

        String[] genderOptions = {"Male", "Female", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);

        // Load data from sever
        // etName.setText(...)

        btnSave.setOnClickListener(v -> {
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
            finish();
        });
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
