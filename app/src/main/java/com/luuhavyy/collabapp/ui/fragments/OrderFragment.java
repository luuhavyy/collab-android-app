package com.luuhavyy.collabapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.ui.adapters.OrderAdapter;
import com.luuhavyy.collabapp.ui.viewmodels.OrderViewModel;

public class OrderFragment extends Fragment {
    private OrderViewModel orderViewModel;
    private OrderAdapter orderAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        RecyclerView recyclerOrders = view.findViewById(R.id.recycler_orders);
        orderAdapter = new OrderAdapter(getContext());
        recyclerOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerOrders.setAdapter(orderAdapter);

        // Khởi tạo ViewModel
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        // Observe dữ liệu
        orderViewModel.getOrdersLiveData().observe(getViewLifecycleOwner(), orders -> {
            orderAdapter.setOrders(orders);
        });

        // Mặc định load tab Pending
        orderViewModel.fetchOrdersByStatusForCurrentUser("Pending");

        // Xử lý tab
        TextView tabPending = view.findViewById(R.id.tab_pending);
        TextView tabShipping = view.findViewById(R.id.tab_shipping);
        TextView tabDelivered = view.findViewById(R.id.tab_delivered);

        tabPending.setOnClickListener(v -> orderViewModel.fetchOrdersByStatusForCurrentUser("Pending"));
        tabShipping.setOnClickListener(v -> orderViewModel.fetchOrdersByStatusForCurrentUser("Shipping"));
        tabDelivered.setOnClickListener(v -> orderViewModel.fetchOrdersByStatusForCurrentUser("Delivered"));

        return view;
    }
}

