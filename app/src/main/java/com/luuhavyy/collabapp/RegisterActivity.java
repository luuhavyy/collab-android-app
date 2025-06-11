package com.luuhavyy.collabapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import connectors.UserConnector;
import models.Users;

public class RegisterActivity extends AppCompatActivity {
    private EditText edtPhoneNumberRegister;
    private Button btnRegister;
    private LinearLayout mainLayout;
    private UserConnector userConnector;

    private View registerFormLayout;
    private View verificationLayout;
    private DatabaseReference userLookupRef;
    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userConnector = new UserConnector();
        initializeViews();
        setupLayouts();
        setupPhoneNumberTextWatcher();
        mAuth = FirebaseAuth.getInstance();
        userLookupRef = FirebaseDatabase.getInstance().getReference("userLookup/usernames");
        usersRef = FirebaseDatabase.getInstance().getReference("users");
    }

    private void initializeViews() {
        edtPhoneNumberRegister = findViewById(R.id.edtPhoneNumberRegister);
        btnRegister = findViewById(R.id.btnRegister);
        mainLayout = findViewById(R.id.main);
    }

    private void setupLayouts() {
        registerFormLayout = getLayoutInflater().inflate(R.layout.item_register_form, mainLayout, false);
        verificationLayout = getLayoutInflater().inflate(R.layout.item_verification_password, mainLayout, false);
    }

    private void setupPhoneNumberTextWatcher() {
        edtPhoneNumberRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFieldsForEmptyValues();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnRegister.setOnClickListener(v -> verifyPhoneNumber());
    }

    private void checkFieldsForEmptyValues() {
        String phone = edtPhoneNumberRegister.getText().toString().trim();
        boolean isEnabled = !phone.isEmpty();
        btnRegister.setEnabled(isEnabled);
        btnRegister.setBackgroundTintList(ColorStateList.valueOf(
                isEnabled ? getResources().getColor(R.color.green_button)
                        : getResources().getColor(R.color.gray_button)));
    }

    private void verifyPhoneNumber() {
        String phoneNumber = edtPhoneNumberRegister.getText().toString().trim();

        if (TextUtils.isEmpty(phoneNumber)) {
            edtPhoneNumberRegister.setError("Phone number is required");
            return;
        }

        userConnector.getUserByPhoneNumber(phoneNumber, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(RegisterActivity.this,
                            "This phone number is already in use, please enter new phone number",
                            Toast.LENGTH_SHORT).show();
                } else {
                    showVerificationLayout(phoneNumber);
                    Toast.makeText(RegisterActivity.this,
                            "Verification code sent: 1234",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(RegisterActivity.this,
                        "Error checking phone number: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showVerificationLayout(String phoneNumber) {
        mainLayout.removeAllViews();
        mainLayout.addView(verificationLayout);

        TextView txtPhoneNumber = verificationLayout.findViewById(R.id.txtPhoneNumber);
        txtPhoneNumber.setText("Is this your phone number, please clarify +" + phoneNumber);

        TextView txtChangePhone = verificationLayout.findViewById(R.id.txtChangePhone);
        txtChangePhone.setOnClickListener(v -> {
            mainLayout.removeAllViews();
            setContentView(R.layout.activity_register);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
            initializeViews();
            setupLayouts();
            setupPhoneNumberTextWatcher();
        });

        TextView txtResendCode = verificationLayout.findViewById(R.id.textView12);
        txtResendCode.setOnClickListener(v -> {
            Toast.makeText(this, "New verification code sent: 2345", Toast.LENGTH_SHORT).show();
        });

        EditText edtFirst = verificationLayout.findViewById(R.id.edtFirstNumber);
        EditText edtSecond = verificationLayout.findViewById(R.id.edtSecondNumber);
        EditText edtThird = verificationLayout.findViewById(R.id.edtThirdNumber);
        EditText edtLast = verificationLayout.findViewById(R.id.edtLastNumber);

        // Setup text watchers for verification code fields
        setupVerificationCodeTextWatchers(edtFirst, edtSecond, edtThird, edtLast);

        Button btnContinue = verificationLayout.findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(v -> {
            String code = edtFirst.getText().toString() +
                    edtSecond.getText().toString() +
                    edtThird.getText().toString() +
                    edtLast.getText().toString();

            if (code.equals("1234") || code.equals("2345")) {
                showRegistrationForm(phoneNumber);
            } else {
                Toast.makeText(this, "Invalid verification code", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupVerificationCodeTextWatchers(EditText... fields) {
        for (int i = 0; i < fields.length; i++) {
            final int currentIndex = i;
            fields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && currentIndex < fields.length - 1) {
                        fields[currentIndex + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void showRegistrationForm(String phoneNumber) {
        mainLayout.removeAllViews();
        mainLayout.addView(registerFormLayout);
        setupRegistrationForm(phoneNumber);
    }

    private void setupRegistrationForm(String phoneNumber) {
        EditText edtFullName = registerFormLayout.findViewById(R.id.edtFullName);
        EditText edtUsername = registerFormLayout.findViewById(R.id.edtUserName);
        EditText edtEmail = registerFormLayout.findViewById(R.id.edtEmail);
        EditText edtGender = registerFormLayout.findViewById(R.id.edtGender);
        EditText edtPassword = registerFormLayout.findViewById(R.id.edtPassword);
        EditText edtConfirmPassword = registerFormLayout.findViewById(R.id.edtConfirmPassword);
        Button btnConfirm = registerFormLayout.findViewById(R.id.btnConfirmation);

        // Set initial button state
        checkFormFields(btnConfirm, edtFullName, edtUsername, edtEmail,
                edtGender, edtPassword, edtConfirmPassword);

        TextWatcher formTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFormFields(btnConfirm, edtFullName, edtUsername, edtEmail,
                        edtGender, edtPassword, edtConfirmPassword);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        edtFullName.addTextChangedListener(formTextWatcher);
        edtUsername.addTextChangedListener(formTextWatcher);
        edtEmail.addTextChangedListener(formTextWatcher);
        edtGender.addTextChangedListener(formTextWatcher);
        edtPassword.addTextChangedListener(formTextWatcher);
        edtConfirmPassword.addTextChangedListener(formTextWatcher);

        btnConfirm.setOnClickListener(v -> {
            registerUser(phoneNumber, edtFullName, edtUsername, edtEmail,
                    edtGender, edtPassword, edtConfirmPassword);
        });
    }

    private void checkFormFields(Button btnConfirm, EditText... fields) {
        boolean allFilled = true;
        for (EditText field : fields) {
            if (field.getText().toString().trim().isEmpty()) {
                allFilled = false;
                break;
            }
        }

        btnConfirm.setEnabled(allFilled);
        btnConfirm.setBackgroundTintList(ColorStateList.valueOf(
                allFilled ? getResources().getColor(R.color.green_button)
                        : getResources().getColor(R.color.gray_button)));
    }

    private void registerUser(String phoneNumber, EditText edtFullName, EditText edtUsername,
                              EditText edtEmail, EditText edtGender, EditText edtPassword,
                              EditText edtConfirmPassword) {
        String fullName = edtFullName.getText().toString().trim();
        String username = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String gender = edtGender.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        resetFieldErrors(edtFullName, edtUsername, edtEmail, edtGender, edtPassword, edtConfirmPassword);

        boolean hasError = false;

        if (TextUtils.isEmpty(fullName)) {
            setFieldError(edtFullName, getString(R.string.error_fullname_required));
            hasError = true;
        }

        if (TextUtils.isEmpty(username)) {
            setFieldError(edtUsername, getString(R.string.error_username_required));
            hasError = true;
        }

        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setFieldError(edtEmail, getString(R.string.error_valid_email_required));
            hasError = true;
        }

        if (TextUtils.isEmpty(gender)) {
            setFieldError(edtGender, getString(R.string.error_gender_required));
            hasError = true;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            setFieldError(edtPassword, getString(R.string.error_password_length));
            hasError = true;
        }

        if (!password.equals(confirmPassword)) {
            setFieldError(edtConfirmPassword, getString(R.string.error_password_mismatch));
            hasError = true;
        }

        if (hasError) {
            showErrorDialog(getString(R.string.title_alert_register_header_fail),
                    getString(R.string.error_please_fill_all_fields));
            return;
        }

        userConnector.getUserByUsername(username, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    setFieldError(edtUsername, getString(R.string.error_username_exists));
                    showErrorDialog(getString(R.string.title_alert_register_header_fail),
                            getString(R.string.error_username_exists));
                } else {
                    userConnector.getUserByEmail(email, new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                setFieldError(edtEmail, getString(R.string.error_email_exists));
                                showErrorDialog(getString(R.string.title_alert_register_header_fail),
                                        getString(R.string.error_email_exists));
                            } else {
                                createNewUser(phoneNumber, fullName, username, email, gender, password);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            showErrorDialog(getString(R.string.title_alert_register_header_fail),
                                    getString(R.string.error_database_connection));
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                showErrorDialog(getString(R.string.title_alert_register_header_fail),
                        getString(R.string.error_database_connection));
            }
        });
    }

    private void resetFieldErrors(EditText... fields) {
        for (EditText field : fields) {
            field.setError(null);
            field.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.default_edittext_color)));
        }
    }

    private void setFieldError(EditText field, String errorMessage) {
        field.setError(errorMessage);
        field.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.error_red)));
    }

    private void showErrorDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        Resources res = getResources();

        builder.setTitle(title);
        builder.setIcon(R.mipmap.ic_error);
        builder.setMessage(message);

        builder.setPositiveButton(res.getText(R.string.title_alert_register_back), (dialog, which) -> dialog.dismiss());

        builder.setCancelable(false);
        builder.show();
    }

    private void createNewUser(String phoneNumber, String fullName, String username,
                               String email, String gender, String password) {
        Users newUser = new Users();
        newUser.setPhonenumber(phoneNumber);
        newUser.setName(fullName);
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setGender(gender);
        newUser.setPassword(password);

        // Tạo user trong Firebase Auth trước
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();
                        newUser.setUserid(uid);

                        // Lưu user vào Realtime Database
                        usersRef.child(uid).setValue(newUser)
                                .addOnCompleteListener(dbTask -> {
                                    if (dbTask.isSuccessful()) {
                                        // Thêm vào node tra cứu username
                                        userLookupRef.child(username.toLowerCase()).setValue(uid)
                                                .addOnCompleteListener(lookupTask -> {
                                                    if (lookupTask.isSuccessful()) {
                                                        showSuccessDialog(newUser);
                                                    } else {
                                                        showErrorDialog();
                                                    }
                                                });
                                    } else {
                                        showErrorDialog();
                                    }
                                });
                    } else {
                        showErrorDialog();
                    }
                });
    }

    private void showSuccessDialog(Users user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        Resources res = getResources();
        builder.setTitle(res.getText(R.string.title_alert_register));
        builder.setIcon(R.mipmap.ic_success);
        builder.setMessage(res.getText(R.string.title_alert_register_header_success)+"\n"+ res.getText(R.string.title_alert_register_description));
        builder.setPositiveButton(res.getText(R.string.title_alert_register_continue), (dialog, which) -> {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.putExtra("USER_DATA", user);
            startActivity(intent);
            finish();
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        Resources res = getResources();

        builder.setTitle(res.getText(R.string.title_alert_register));
        builder.setIcon(R.mipmap.ic_error);
        builder.setMessage(
                res.getText(R.string.title_alert_register_header_fail) + "\n" +
                        res.getText(R.string.title_alert_register_description_fail)
        );

        builder.setPositiveButton(res.getText(R.string.title_alert_register_back), (dialog, which) -> {});

        builder.setCancelable(false);
        builder.show();
    }

    public void do_back_login(View view) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}