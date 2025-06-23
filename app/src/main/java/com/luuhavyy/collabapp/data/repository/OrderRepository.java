package com.luuhavyy.collabapp.data.repository;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luuhavyy.collabapp.data.model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private final DatabaseReference orderRef;

    public OrderRepository() {
        orderRef = FirebaseDatabase.getInstance().getReference("orders");
    }

    // Callback interface
    public interface OrdersCallback {
        void onOrdersLoaded(List<Order> orders);

        void onError(String error);
    }

    // Get all (Pending, Shipping, Delivered)
    public void getOrdersByStatusAndUser(String status, String userId, OrdersCallback callback) {
        orderRef.orderByChild("status").equalTo(status)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Order> orderList = new ArrayList<>();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            Order order = item.getValue(Order.class);
                            // Chỉ lấy order có đúng userId
                            if (order != null && order.getUserid() != null && order.getUserid().equals(userId)) {
                                orderList.add(order);
                            }
                        }
                        callback.onOrdersLoaded(orderList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.getMessage());
                    }
                });
    }

    public void getOrderById(String orderId, ValueEventListener listener) {
        orderRef.child(orderId).addListenerForSingleValueEvent(listener);
    }
}
