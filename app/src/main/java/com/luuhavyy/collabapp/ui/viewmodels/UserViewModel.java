package com.luuhavyy.collabapp.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.luuhavyy.collabapp.data.model.User;
import com.luuhavyy.collabapp.data.repository.UserRepository;

import lombok.Getter;
import lombok.Setter;

public class UserViewModel extends ViewModel {
    private final UserRepository repository = new UserRepository();
    @Getter
    @Setter
    private LiveData<User> user;

    public UserViewModel() {
        user = repository.getUserProfile();
    }
}
