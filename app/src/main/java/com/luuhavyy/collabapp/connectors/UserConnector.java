package com.luuhavyy.collabapp.connectors;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserConnector {
    private final DatabaseReference databaseReference;

    public UserConnector() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users");
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public void getUserByUsername(String username, ValueEventListener listener) {
        databaseReference.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(listener);
    }

    public void getUserByPhoneNumber(String phoneNumber, ValueEventListener listener) {
        databaseReference.orderByChild("phonenumber").equalTo(phoneNumber)
                .addListenerForSingleValueEvent(listener);
    }

    public void getUserByEmail(String email, ValueEventListener listener) {
        databaseReference.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(listener);
    }

    public Task<Void> updateUserPassword(String userId, String newPassword) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        Map<String, Object> updates = new HashMap<>();
        updates.put("password", newPassword);
        return userRef.updateChildren(updates);
    }


    public void resetPasswordWithToken(String userId, String newPassword, String token, OnCompleteListener<Void> listener) {
        DatabaseReference tokenRef = FirebaseDatabase.getInstance()
                .getReference("passwordResetTokens")
                .child(userId)
                .child(token);

        tokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Nếu token hợp lệ, cập nhật mật khẩu
                    DatabaseReference userRef = FirebaseDatabase.getInstance()
                            .getReference("users")
                            .child(userId)
                            .child("password");

                    userRef.setValue(newPassword)
                            .addOnCompleteListener(task -> {
                                // Xóa token sau khi sử dụng
                                tokenRef.removeValue();
                                listener.onComplete(task);
                            });
                } else {
                    listener.onComplete(Tasks.forException(new Exception("Invalid reset token")));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                listener.onComplete(Tasks.forException(error.toException()));
            }
        });
    }


    public void sendPasswordResetEmail(String email, OnCompleteListener<Void> listener) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(listener);
    }
}