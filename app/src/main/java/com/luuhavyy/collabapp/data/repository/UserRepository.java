package com.luuhavyy.collabapp.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.ValueEventListener;
import com.luuhavyy.collabapp.data.model.User;
import com.luuhavyy.collabapp.data.remote.UserRemoteDataSource;

public class UserRepository {
    private final UserRemoteDataSource userRemoteDataSource;

    public UserRepository() {
        userRemoteDataSource = new UserRemoteDataSource();
    }

    public void getAllUsers(ValueEventListener listener) {
        userRemoteDataSource.getAllUsers(listener);
    }

    public void getUserById(String uid, ValueEventListener listener) {
        userRemoteDataSource.getUserById(uid, listener);
    }

    public LiveData<User> getUserProfile() {
        MutableLiveData<User> userData = new MutableLiveData<>();

        User user = new User();
        userData.setValue(user);

        return userData;
    }

    public void updateProfileImageBase64(String userId, String base64Image, Runnable onSuccess, Runnable onError) {
        userRemoteDataSource.updateProfilePictureBase64(userId, base64Image, onSuccess, onError);
    }
}