package com.luuhavyy.collabapp.ui.fragments;

import android.content.Intent;
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
import com.luuhavyy.collabapp.ui.activities.OrderDetailActivity;
import com.luuhavyy.collabapp.ui.activities.PromotionActivity;
import com.luuhavyy.collabapp.ui.adapters.NotificationAdapter;
import com.luuhavyy.collabapp.ui.viewmodels.NotificationViewModel;
import com.luuhavyy.collabapp.utils.AuthUtil;

public class NotificationFragment extends Fragment {
    private NotificationViewModel viewModel;
    private NotificationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        TextView tvEmpty = view.findViewById(R.id.tv_empty);

        adapter = new NotificationAdapter(notification -> {
            if ("Order Status".equalsIgnoreCase(notification.getType())) {
                Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                intent.putExtra("order_id", notification.getRelatedid());
                startActivity(intent);
            } else if ("Promotion".equalsIgnoreCase(notification.getType())) {
                Intent intent = new Intent(getContext(), PromotionActivity.class);
                intent.putExtra("promotion_id", notification.getRelatedid());
                startActivity(intent);
            }
        });


        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

        String authId = AuthUtil.getCurrentUser().getUid();
        viewModel.loadNotificationsByAuthId(authId);

        viewModel.getNotifications().observe(getViewLifecycleOwner(), notifications -> {
            adapter.submitList(notifications);
            tvEmpty.setVisibility(notifications.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }
}

