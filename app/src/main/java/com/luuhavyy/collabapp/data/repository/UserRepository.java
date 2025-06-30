package com.luuhavyy.collabapp.data.repository;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luuhavyy.collabapp.data.model.User;
import com.luuhavyy.collabapp.data.remote.UserRemoteDataSource;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

    public interface UserIdCallback {
        void onUserIdLoaded(String userId);
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

    public void loadUserIdByAuthId(String authId, UserIdCallback callback) {
        DatabaseReference mappingRef = FirebaseDatabase.getInstance()
                .getReference("firebaseUidToUserId").child(authId);
        mappingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userId = snapshot.getValue(String.class);
                callback.onUserIdLoaded(userId); // Có thể null nếu không có
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onUserIdLoaded(null); // hoặc tạo hàm onError, tuỳ nhu cầu
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

    public void logUserActivity(String userId, String action, String targetId) {
        DatabaseReference userActivityRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("useractivity");

        userActivityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Tìm max id hiện tại
                long maxId = -1;
                int viewProductCount = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    // Kiểm tra key là số
                    try {
                        long id = Long.parseLong(child.getKey());
                        if (id > maxId) maxId = id;
                    } catch (Exception ignored) {
                    }
                    // Đếm số lần "View Product"
                    Object actionVal = child.child("action").getValue();
                    if (actionVal != null && actionVal.toString().equals("View Product")) {
                        viewProductCount++;
                    }
                }
                // id mới là maxId + 1
                String activityId = String.valueOf(maxId + 1);

                String now = OffsetDateTime.now(ZoneOffset.ofHours(7))
                        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

                // Tạo activity object
                Map<String, Object> activity = new HashMap<>();
                activity.put("activityid", "act" + activityId);
                activity.put("action", action);
                activity.put("targetid", targetId);
                activity.put("timestamp", now);

                // Thêm activity mới vào node với id mới
                userActivityRef.child(activityId).setValue(activity);

                // Nếu số "View Product" >= 4 và lần này tiếp tục là "View Product" (tổng đủ 5)
                if (action.equals("View Product") && (viewProductCount + 1) >= 5) {
                    callWebhook(userId); // Call webhook n8n
                }
                if (action.equals("Added to Cart")) {
                    callWebhook(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    public void callWebhook(String userId) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        String json = "{\"userid\":\"" + userId + "\"}";
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url("https://luuhahavy.app.n8n.cloud/webhook/n8n_dynamic_promo")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                response.close();
            }
        });
    }
}