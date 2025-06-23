package com.luuhavyy.collabapp.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.luuhavyy.collabapp.data.model.Order;
import com.luuhavyy.collabapp.data.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;

public class OrderViewModel extends ViewModel {
    private final MutableLiveData<List<Order>> ordersLiveData = new MutableLiveData<>();
    private final OrderRepository orderRepository = new OrderRepository();

    public LiveData<List<Order>> getOrdersLiveData() {
        return ordersLiveData;
    }

    public void fetchOrdersByStatusForCurrentUser(String status) {
        String userId = FirebaseAuth.getInstance().getUid();
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
    }
}
