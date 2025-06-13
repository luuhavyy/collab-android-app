package com.luuhavyy.collabapp.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.ui.activities.RegisterActivity;
import com.luuhavyy.collabapp.data.model.User;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUserName;
    private EditText edtPassword;
    private CheckBox chkSaveInfor;
    private Button btnSignIn;
    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        edtUserName = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        chkSaveInfor = findViewById(R.id.chkSaveInfor);
        btnSignIn = findViewById(R.id.btnSignIn);

        // Reference to users node
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        edtUserName.addTextChangedListener(loginTextWatcher);
        edtPassword.addTextChangedListener(loginTextWatcher);

        btnSignIn.setOnClickListener(v -> performLogin());
    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkFieldsForEmptyValues();
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    private void checkFieldsForEmptyValues() {
        String username = edtUserName.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (!username.isEmpty() && !password.isEmpty()) {
            btnSignIn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green_button)));
        } else {
            btnSignIn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray_button)));
        }
    }

    private void performLogin() {
        String usernameInput = edtUserName.getText().toString().trim();
        String passwordInput = edtPassword.getText().toString();

        if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
            Toast.makeText(this, getString(R.string.notification_empty_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        btnSignIn.setEnabled(false);
        btnSignIn.setText(getString(R.string.title_login_button_signin));

        // First try to authenticate directly with username as email
        mAuth.signInWithEmailAndPassword(usernameInput, passwordInput)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Success - user used email as username
                        onLoginSuccess(mAuth.getCurrentUser().getUid());
                    } else {
                        // If failed, try querying for username
                        tryUsernameLogin(usernameInput, passwordInput);
                    }
                });
    }

    private void tryUsernameLogin(String username, String password) {
        Query query = usersRef.orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        if (user != null) {
                            // Attempt to sign in with the actual email
                            mAuth.signInWithEmailAndPassword(user.getEmail(), password)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            onLoginSuccess(userSnapshot.getKey());
                                        } else {
                                            handleLoginFailure();
                                        }
                                    });
                            return;
                        }
                    }
                }
                handleLoginFailure();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                handleLoginFailure();
            }
        });
    }

    private void onLoginSuccess(String userId) {
        if (chkSaveInfor.isChecked()) {
            saveLoginInformation();
        }

        Toast.makeText(LoginActivity.this,
                getString(R.string.notification_login_success),
                Toast.LENGTH_SHORT).show();

        // Create the Firebase UID to internal user ID mapping
        String firebaseUid = mAuth.getCurrentUser().getUid();
        DatabaseReference mappingRef = FirebaseDatabase.getInstance().getReference("firebaseUidToUserId");
        mappingRef.child(firebaseUid).setValue(userId);

        // Get full user data
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Intent intent = new Intent(LoginActivity.this, CartActivity.class);
                intent.putExtra("USER_DATA", user);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Intent intent = new Intent(LoginActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void handleLoginFailure() {
        Toast.makeText(LoginActivity.this,
                getString(R.string.notification_wrong_credentials),
                Toast.LENGTH_SHORT).show();
        resetLoginButton();
    }

    private void resetLoginButton() {
        btnSignIn.setEnabled(true);
        btnSignIn.setText(getString(R.string.title_login_button_signin));
    }

    public void do_exit(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        Resources res = getResources();
        builder.setTitle(res.getText(R.string.title_confirm_exit_title));
        builder.setMessage(res.getText(R.string.title_confirm_exit_message));
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setPositiveButton(res.getText(R.string.title_confirm_exit_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                });

        builder.setNegativeButton(res.getText(R.string.title_confirm_exit_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void saveLoginInformation() {
        SharedPreferences preferences = getSharedPreferences("LOGIN_PREFERENCE", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String usr = edtUserName.getText().toString();
        String pwd = edtPassword.getText().toString();
        boolean isSave = chkSaveInfor.isChecked();
        editor.putString("USER_NAME", usr);
        editor.putString("PASSWORD", pwd);
        editor.putString("FIREBASE_UID", mAuth.getCurrentUser().getUid());
        editor.putBoolean("SAVED", isSave);
        editor.putBoolean("IS_LOGGED_IN", true);
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveLoginInformation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        restoreLoginInformation();
    }

    public void restoreLoginInformation() {
        SharedPreferences preferences = getSharedPreferences("LOGIN_PREFERENCE", MODE_PRIVATE);
        String usr = preferences.getString("USER_NAME", "");
        String pwd = preferences.getString("PASSWORD", "");
        boolean isSave = preferences.getBoolean("SAVED", false);
        if (isSave) {
            edtUserName.setText(usr);
            edtPassword.setText(pwd);
            chkSaveInfor.setChecked(isSave);
        }
    }

    public void do_open_register(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void do_open_forgot_password(View view) {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }
}