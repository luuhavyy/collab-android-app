package com.luuhavyy.collabapp.data.service;

import android.net.Uri;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseImageService {

    public interface UploadCallback {
        void onSuccess(String downloadUrl);
        void onFailure(Exception e);
    }

    public void uploadImage(Uri imageUri, UploadCallback callback) {
        if (imageUri == null) {
            callback.onFailure(new IllegalArgumentException("ImageUri null"));
            return;
        }

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        callback.onSuccess(uri.toString());
                    });
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void saveImageUrlToRealtimeDB(String userId, String url, DatabaseReference.CompletionListener listener) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users/" + userId + "/avatar");
        databaseRef.setValue(url, listener);
    }
}
