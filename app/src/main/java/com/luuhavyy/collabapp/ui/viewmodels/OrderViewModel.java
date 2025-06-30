package com.luuhavyy.collabapp.ui.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.luuhavyy.collabapp.data.model.Order;
import com.luuhavyy.collabapp.data.model.User;
import com.luuhavyy.collabapp.data.repository.OrderRepository;
import com.luuhavyy.collabapp.data.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class OrderViewModel extends ViewModel {
    private final MutableLiveData<List<Order>> ordersLiveData = new MutableLiveData<>();
    private final OrderRepository orderRepository = new OrderRepository();
    private final UserRepository userRepository = new UserRepository();

    public LiveData<List<Order>> getOrdersLiveData() {
        return ordersLiveData;
    }

    public void fetchOrdersByStatusForCurrentUser(String status) {
        String authId = FirebaseAuth.getInstance().getUid();
        if (authId == null) {
            ordersLiveData.postValue(new ArrayList<>());
            return;
        }

        userRepository.loadUserByAuthId(authId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                String userId = (user != null) ? user.getUserid() : null;

                if (userId != null) {
                    orderRepository.getOrdersByStatusAndUser(status, userId, new OrderRepository.OrdersCallback() {
                        @Override
                        public void onOrdersLoaded(List<Order> orders) {
                            ordersLiveData.postValue(orders);
                        }
                        @Override
                        public void onError(String error) {
                            ordersLiveData.postValue(new ArrayList<>());
                        }
                    });
                } else {
                    ordersLiveData.postValue(new ArrayList<>());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ordersLiveData.postValue(new ArrayList<>());
            }
        });
    }
}
