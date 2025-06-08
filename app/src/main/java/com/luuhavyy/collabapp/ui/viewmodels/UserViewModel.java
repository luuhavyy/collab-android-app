package com.luuhavyy.collabapp.ui.viewmodels;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.luuhavyy.collabapp.data.model.User;
import com.luuhavyy.collabapp.data.repository.UserRepository;
import com.luuhavyy.collabapp.utils.ImageUtil;

import java.io.IOException;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class UserViewModel extends ViewModel {
    private final UserRepository userRepository;
    @Getter
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> errorLiveData = new MutableLiveData<>();

    public UserViewModel() {
        userRepository = new UserRepository();
    }

    public void fetchUserById(String uid) {
        userRepository.getUserById(uid, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    userLiveData.setValue(user);
                } else {
                    userLiveData.setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userLiveData.setValue(null);
            }
        });
    }

    public void updateProfilePicture(Context context, Uri imageUri, String userId, Runnable onSuccess, Runnable onError) {
        try {
            String base64 = ImageUtil.uriToBase64(context, imageUri);

            userRepository.updateProfileImageBase64(userId, base64,
                    () -> {
                        fetchUserById(userId);
                        onSuccess.run();
                    },
                    onError
            );

        } catch (IOException e) {
            onError.run();
        }
    }
}
