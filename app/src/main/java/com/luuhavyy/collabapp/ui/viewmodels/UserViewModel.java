package com.luuhavyy.collabapp.ui.viewmodels;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.luuhavyy.collabapp.data.model.User;
import com.luuhavyy.collabapp.data.repository.UserRepository;
import com.luuhavyy.collabapp.utils.ImageUtil;
import com.luuhavyy.collabapp.utils.LoadingHandlerUtil;

import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;

import lombok.Getter;

public class UserViewModel extends ViewModel {
    private final UserRepository userRepository;
    @Getter
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private ValueEventListener userListener;

    public UserViewModel() {
        userRepository = new UserRepository();
    }

    public void listenToUserRealtime(String uid, LoadingHandlerUtil.TaskCallback callback) {
        if (userListener != null) {
            callback.onComplete();
            return;
        }

        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    userLiveData.setValue(user);
                } else {
                    userLiveData.setValue(null);
                }
                callback.onComplete();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userLiveData.setValue(null);
                callback.onComplete();
            }
        };

        userRepository.listenToUserRealtime(uid, userListener);
    }

    public void updateUserInfo(String uid, User updatedUser, Runnable onSuccess, Runnable onError) {
        userRepository.updateUser(uid, updatedUser, onSuccess, onError);
    }

    public void updateProfilePicture(Context context, Uri imageUri, String userId, Runnable onSuccess, Runnable onError) {
        try {
            String base64 = null;
            if (ObjectUtils.isNotEmpty(imageUri)) base64 = ImageUtil.uriToBase64(context, imageUri);

            userRepository.updateProfileImageBase64(userId, base64,
                    onSuccess,
                    onError
            );

        } catch (IOException e) {
            onError.run();
        }
    }

    public void loadUser(String authId, LoadingHandlerUtil.TaskCallback callback) {
        userRepository.loadUserByAuthId(authId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                userLiveData.setValue(user);
                if (callback != null) callback.onComplete();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userLiveData.setValue(null);
                if (callback != null) callback.onComplete();
            }
        });
    }

    public void logUserActivityByAuthId(String authId, String action, String targetId) {
        userRepository.loadUserIdByAuthId(authId, userId -> {
            if (userId != null) {
                logUserActivity(userId, action, targetId);
            }
        });
    }

    public void logUserActivity(String userId, String action, String targetId) {
        userRepository.logUserActivity(userId, action, targetId);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (userListener != null) {
            userRepository.removeUserListener(userListener);
            userListener = null;
        }
    }
}
