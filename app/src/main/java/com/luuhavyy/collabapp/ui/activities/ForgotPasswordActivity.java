package com.luuhavyy.collabapp.ui.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import com.google.firebase.database.DatabaseError;
import com.luuhavyy.collabapp.ui.activities.LoginActivity;
import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.connectors.UserConnector;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText edtPhoneNumber;
    private Button btnReset;
    private LinearLayout mainLayout;
    private UserConnector userConnector;
    private String currentVerificationCode = "1234"; // Default code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userConnector = new UserConnector();
        addViews();
        setupListeners();
    }

    private void addViews() {
        btnReset = findViewById(R.id.btnRegister);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumberRegister);
        mainLayout = findViewById(R.id.main);
    }

    private void setupListeners() {
        btnReset.setOnClickListener(v -> {
            String phoneNumber = edtPhoneNumber.getText().toString().trim();

            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_enter_phone), Toast.LENGTH_SHORT).show();
                return;
            }

            final String finalNormalizedPhone = normalizePhoneNumber(phoneNumber);

            if (finalNormalizedPhone.length() < 10 || !finalNormalizedPhone.matches("^\\+\\d+$")) {
                Toast.makeText(this, getString(R.string.toast_invalid_phone_format), Toast.LENGTH_SHORT).show();
                return;
            }

            userConnector.getUserByPhoneNumber(finalNormalizedPhone, new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        showVerificationScreen(phoneNumber);
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this,
                                getString(R.string.toast_phone_not_registered), Toast.LENGTH_SHORT).show();
                        Log.d("PhoneCheck", "Not found in DB: " + finalNormalizedPhone);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(ForgotPasswordActivity.this,
                            getString(R.string.toast_database_error, error.getMessage()), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("0")) {
            return "+84" + phoneNumber.substring(1);
        } else if (phoneNumber.startsWith("84")) {
            return "+" + phoneNumber;
        } else if (!phoneNumber.startsWith("+")) {
            return "+" + phoneNumber;
        }
        return phoneNumber;
    }

    private void showVerificationScreen(String phoneNumber) {
        mainLayout.removeAllViews();
        View verificationView = getLayoutInflater().inflate(R.layout.item_verification_password, mainLayout, false);
        mainLayout.addView(verificationView);

        TextView txtPhoneNumber = verificationView.findViewById(R.id.txtPhoneNumber);
        txtPhoneNumber.setText(getString(R.string.verify_phone_message, phoneNumber));

        TextView txtChangePhone = verificationView.findViewById(R.id.txtChangePhone);
        txtChangePhone.setOnClickListener(v -> {
            mainLayout.removeAllViews();
            View forgotPasswordView = getLayoutInflater().inflate(R.layout.activity_forgot_password, mainLayout, false);
            mainLayout.addView(forgotPasswordView);
            addViews();
            setupListeners();
        });

        TextView txtResend = verificationView.findViewById(R.id.textView12);
        txtResend.setOnClickListener(v -> {
            currentVerificationCode = "2345";
            Toast.makeText(this, getString(R.string.toast_resend_code, currentVerificationCode), Toast.LENGTH_SHORT).show();
        });

        EditText edtFirst = verificationView.findViewById(R.id.edtFirstNumber);
        EditText edtSecond = verificationView.findViewById(R.id.edtSecondNumber);
        EditText edtThird = verificationView.findViewById(R.id.edtThirdNumber);
        EditText edtLast = verificationView.findViewById(R.id.edtLastNumber);
        Button btnContinue = verificationView.findViewById(R.id.btnContinue);

        edtFirst.setText("1");
        edtSecond.setText("2");
        edtThird.setText("3");
        edtLast.setText("4");

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean allFilled = !edtFirst.getText().toString().isEmpty() &&
                        !edtSecond.getText().toString().isEmpty() &&
                        !edtThird.getText().toString().isEmpty() &&
                        !edtLast.getText().toString().isEmpty();
                btnContinue.setBackgroundTintList(ColorStateList.valueOf(
                        allFilled ? getResources().getColor(R.color.green_button)
                                : getResources().getColor(R.color.gray_button)));

            }
            @Override public void afterTextChanged(Editable s) {}
        };

        edtFirst.addTextChangedListener(watcher);
        edtSecond.addTextChangedListener(watcher);
        edtThird.addTextChangedListener(watcher);
        edtLast.addTextChangedListener(watcher);

        btnContinue.setOnClickListener(v -> {
            String enteredCode = edtFirst.getText().toString() +
                    edtSecond.getText().toString() +
                    edtThird.getText().toString() +
                    edtLast.getText().toString();

            if (enteredCode.length() == 4) {
                if (enteredCode.equals(currentVerificationCode)) {
                    showUpdatePasswordScreen(phoneNumber);
                } else {
                    Toast.makeText(this, getString(R.string.toast_invalid_code), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.toast_code_not_complete), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUpdatePasswordScreen(String phoneNumber) {
        mainLayout.removeAllViews();
        View updatePasswordView = getLayoutInflater().inflate(R.layout.item_update_password, mainLayout, false);
        mainLayout.addView(updatePasswordView);

        EditText edtNew = updatePasswordView.findViewById(R.id.editTextTextPassword);
        EditText edtConfirm = updatePasswordView.findViewById(R.id.editTextTextPassword2);
        Button btnSave = updatePasswordView.findViewById(R.id.button);

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean bothFilled = !edtNew.getText().toString().isEmpty() &&
                        !edtConfirm.getText().toString().isEmpty();
                btnSave.setBackgroundTintList(ColorStateList.valueOf(
                        bothFilled ? getResources().getColor(R.color.green_button)
                                : getResources().getColor(R.color.gray_button)));
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        edtNew.addTextChangedListener(watcher);
        edtConfirm.addTextChangedListener(watcher);

        btnSave.setOnClickListener(v -> {
            String newPass = edtNew.getText().toString();
            String confirmPass = edtConfirm.getText().toString();


            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, getString(R.string.toast_password_mismatch), Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPass.length() < 6) {
                Toast.makeText(this, getString(R.string.toast_password_too_short), Toast.LENGTH_SHORT).show();
                return;
            }

            final String normalizedPhone = normalizePhoneNumber(phoneNumber);

            userConnector.getUserByPhoneNumber(normalizedPhone, new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (com.google.firebase.database.DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String userId = userSnapshot.getKey();
                            userConnector.updateUserPassword(userId, newPass)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(ForgotPasswordActivity.this,
                                                getString(R.string.toast_password_updated), Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(ForgotPasswordActivity.this,
                                                getString(R.string.toast_update_failed, e.getMessage()), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this,
                                getString(R.string.toast_user_not_found), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(com.google.firebase.database.DatabaseError error) {
                    Toast.makeText(ForgotPasswordActivity.this,
                            getString(R.string.toast_database_error, error.getMessage()), Toast.LENGTH_SHORT).show();
                }
            });

        });
    }

    public void do_back_login(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
