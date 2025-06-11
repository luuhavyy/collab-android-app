package com.luuhavyy.collabapp.data.remote;

import com.google.firebase.database.*;
import com.luuhavyy.collabapp.data.model.User;

public class UserRemoteDataSource {
    private final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");

    public ValueEventListener getAllUsersRealtime(ValueEventListener listener) {
        userRef.addValueEventListener(listener);
        return listener;
    }

    public ValueEventListener getUserByIdRealtime(String uid, ValueEventListener listener) {
        userRef.child(uid).addValueEventListener(listener);
        return listener;
    }

    public void updateUser(String uid, User user, Runnable onSuccess, Runnable onError) {
        userRef.child(uid).setValue(user)
                .addOnSuccessListener(unused -> onSuccess.run())
                .addOnFailureListener(e -> onError.run());
    }

    public void removeListener(ValueEventListener listener) {
        userRef.removeEventListener(listener);
    }

    public void removeListenerById(String uid, ValueEventListener listener) {
        userRef.child(uid).removeEventListener(listener);
    }

    public void getAllUsers(ValueEventListener listener) {
        userRef.addListenerForSingleValueEvent(listener);
    }

    public void getUserById(String uid, ValueEventListener listener) {
        userRef.child(uid).addListenerForSingleValueEvent(listener);
    }

    public void updateProfilePictureBase64(String userId, String base64Image, Runnable onSuccess, Runnable onError) {
        userRef.child(userId).child("profilepicture").setValue(base64Image)
                .addOnSuccessListener(unused -> onSuccess.run())
                .addOnFailureListener(e -> onError.run());
    }
}
