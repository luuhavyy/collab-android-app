package com.luuhavyy.collabapp.connectors;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.luuhavyy.collabapp.data.model.Order;
import com.luuhavyy.collabapp.data.model.User;

public class CheckoutConnector {
    private DatabaseReference databaseReference;

    public CheckoutConnector() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void updateUserInformation(String userId, User user) {
        databaseReference.child("users").child(userId).setValue(user);
    }

    public void createOrder(Order order) {
        databaseReference.child("orders").child(order.getOrderid()).setValue(order);
    }
}