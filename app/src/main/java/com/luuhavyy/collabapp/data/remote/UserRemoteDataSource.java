package com.luuhavyy.collabapp.data.remote;


import com.google.firebase.database.*;

public class UserRemoteDataSource {
    private final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");

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
