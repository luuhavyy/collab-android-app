package com.luuhavyy.collabapp.ui.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luuhavyy.collabapp.data.model.Notification;
import com.luuhavyy.collabapp.data.model.NotificationType;
import com.luuhavyy.collabapp.data.model.User;
import com.luuhavyy.collabapp.data.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationViewModel extends ViewModel {
    private final MutableLiveData<List<Notification>> notifications = new MutableLiveData<>(new ArrayList<>());
    private final UserRepository userRepo = new UserRepository();
    private DatabaseReference ref;

    public LiveData<List<Notification>> getNotifications() {
        return notifications;
    }

    public void loadNotificationsByAuthId(String authId) {
        userRepo.loadUserByAuthId(authId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                String userId = user != null ? user.getUserid() : null;
                if (userId != null) {
                    listenNotificationsByAuthId(userId);
                } else {
                    notifications.setValue(new ArrayList<>());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                notifications.setValue(new ArrayList<>());
            }
        });
    }

    public void listenNotificationsByAuthId(String userId) {
        ref = FirebaseDatabase.getInstance().getReference("notification");
        ref.orderByChild("userid").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Notification> notiList = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Notification noti = child.getValue(Notification.class);
                            if (noti != null) {
                                notiList.add(noti);
                            }
                        }
                        Collections.sort(notiList, (a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
                        notifications.setValue(notiList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (ref != null) ref.onDisconnect();
    }
}
