package com.luuhavyy.collabapp.data.repository;

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
}