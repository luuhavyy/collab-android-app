package com.luuhavyy.collabapp.ui.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
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
import androidx.annotation.NonNull;
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
import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.connectors.UserConnector;

import java.util.concurrent.atomic.AtomicBoolean;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText edtPhoneNumber;
    private Button btnReset;
    private LinearLayout mainLayout;
    private UserConnector userConnector;
    private String currentVerificationCode = "1234"; // Default code
    private DatabaseReference databaseReference;

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
        databaseReference = FirebaseDatabase.getInstance().getReference();
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

            userConnector.getUserByPhoneNumber(finalNormalizedPhone, new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
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
        txtChangePhone.setOnClickListener(v -> recreate());

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

            resetPasswordWithPhoneNumber(phoneNumber, newPass);
        });
    }

    private void resetPasswordWithPhoneNumber(String phoneNumber, String newPassword) {
        final String normalizedPhone = normalizePhoneNumber(phoneNumber);

        databaseReference.child("users").orderByChild("phonenumber").equalTo(normalizedPhone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                String userId = userSnapshot.getKey();
                                String userEmail = userSnapshot.child("email").getValue(String.class);

                                // Tạo token reset (sử dụng newPassword làm token để đơn giản)
                                String resetToken = newPassword; // Sử dụng mật khẩu mới làm token
                                DatabaseReference tokenRef = databaseReference
                                        .child("passwordResetTokens")
                                        .child(userId)
                                        .child(resetToken);

                                // Bước 1: Tạo token trước
                                tokenRef.setValue(true).addOnCompleteListener(tokenTask -> {
                                    if (tokenTask.isSuccessful()) {
                                        // Bước 2: Cập nhật mật khẩu trong Database
                                        DatabaseReference userRef = databaseReference
                                                .child("users")
                                                .child(userId)
                                                .child("password");

                                        userRef.setValue(newPassword).addOnCompleteListener(passTask -> {
                                            // Bước 3: Xóa token sau khi dùng
                                            tokenRef.removeValue();

                                            if (passTask.isSuccessful()) {
                                                // Bước 4: Cập nhật Firebase Auth
                                                updateFirebaseAuthPassword(userEmail, newPassword, userEmail);
                                            } else {
                                                Toast.makeText(ForgotPasswordActivity.this,
                                                        "Failed to update database password: " + passTask.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(ForgotPasswordActivity.this,
                                                "Failed to create reset token",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "User not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Thêm phương thức mới để cập nhật Firebase Auth
    private void updateFirebaseAuthPassword(String email, String newPassword, String userEmailForSuccessHandler) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Gửi yêu cầu reset mật khẩu qua email (bắt buộc để Firebase Auth cho phép thay đổi mật khẩu)
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(resetTask -> {
                    if (resetTask.isSuccessful()) {
                        // Sau khi gửi email reset thành công, đăng nhập bằng mật khẩu cũ (nếu có)
                        // Lưu ý: Đoạn này giả định người dùng đã click vào link reset trong email
                        // Trong thực tế cần có flow phức tạp hơn

                        // Đăng nhập bằng email và mật khẩu mới để cập nhật Firebase Auth
                        auth.signInWithEmailAndPassword(email, newPassword)
                                .addOnCompleteListener(loginTask -> {
                                    if (loginTask.isSuccessful()) {
                                        handlePasswordUpdateSuccess(userEmailForSuccessHandler);
                                    } else {
                                        Toast.makeText(ForgotPasswordActivity.this,
                                                "Database password updated but failed to update Firebase Auth: " + loginTask.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                        goToLoginScreen();
                                    }
                                });
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Database password updated but failed to update Firebase Auth: " + resetTask.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        goToLoginScreen();
                    }
                });
    }

    private void handlePasswordUpdateSuccess(String userEmail) {
        if (userEmail != null && !userEmail.isEmpty()) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail)
                    .addOnCompleteListener(emailTask -> {
                        String message = emailTask.isSuccessful()
                                ? "Password changed! Check your email."
                                : "Password changed but failed to send email";
                        Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                        goToLoginScreen();
                    });
        } else {
            Toast.makeText(ForgotPasswordActivity.this,
                    "Password changed successfully!",
                    Toast.LENGTH_SHORT).show();
            goToLoginScreen();
        }
    }

    private String generateRandomToken() {
        return String.format("%06d", (int)(Math.random() * 1000000));
    }

    private void goToLoginScreen() {
        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
        finish();
    }

    public void do_back_login(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}