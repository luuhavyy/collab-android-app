package com.luuhavyy.collabapp.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.ui.activities.EditInformationActivity;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = view.findViewById(R.id.list_view);
        String[] menuItems = requireContext().getResources().getStringArray(R.array.profile_menu_items);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                menuItems
        );
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, v, position, id) -> {
            if (position == 0) { // Edit Information
                Intent intent = new Intent(getActivity(), EditInformationActivity.class);
                startActivity(intent);
            }
        });

        TextView logout = view.findViewById(R.id.tv_logout);
        logout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
        });
    }
}
