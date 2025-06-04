package com.luuhavyy.collabapp.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.luuhavyy.collabapp.data.model.User;

public class UserRepository {
    public LiveData<User> getUserProfile() {
        MutableLiveData<User> userData = new MutableLiveData<>();

        User user = new User();
        userData.setValue(user);

        return userData;
    }
}