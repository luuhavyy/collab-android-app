package com.luuhavyy.collabapp.connectors;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        return databaseReference.child(userId).child("password").setValue(newPassword);
    }
    public void sendPasswordResetEmail(String email, OnCompleteListener<Void> listener) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(listener);
    }
}