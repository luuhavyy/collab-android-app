package com.luuhavyy.collabapp.data.repository;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luuhavyy.collabapp.data.model.User;
import com.luuhavyy.collabapp.data.remote.UserRemoteDataSource;

public class UserRepository {
    private final UserRemoteDataSource remoteDataSource;

    public UserRepository() {
        remoteDataSource = new UserRemoteDataSource();
    }

    public void listenToUserRealtime(String uid, ValueEventListener listener) {
        remoteDataSource.getUserByIdRealtime(uid, listener);
    }

    public void removeUserListener(ValueEventListener listener) {
        remoteDataSource.removeListener(listener);
    }

    public void updateUser(String uid, User user, Runnable onSuccess, Runnable onError) {
        remoteDataSource.updateUser(uid, user, onSuccess, onError);
    }

    public void updateProfileImageBase64(String userId, String base64Image, Runnable onSuccess, Runnable onError) {
        remoteDataSource.updateProfilePictureBase64(userId, base64Image, onSuccess, onError);
    }

    public interface UserCallback {
        void onUserLoaded(User user);

        void onError(String error);
    }

    public void loadUserByAuthId(String authId, ValueEventListener listener) {
        DatabaseReference mappingRef = FirebaseDatabase.getInstance()
                .getReference("firebaseUidToUserId").child(authId);
        mappingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userId = snapshot.getValue(String.class);
                if (userId == null) {
                    listener.onCancelled(DatabaseError.fromException(new Exception("No userId found for this authId")));
                    return;
                }
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                userRef.addListenerForSingleValueEvent(listener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onCancelled(error);
            }
        });
    }

    public void getUserById(String userId, UserCallback callback) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                callback.onUserLoaded(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }
}