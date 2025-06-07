package com.luuhavyy.collabapp.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.ui.activities.EditInformationActivity;
import com.luuhavyy.collabapp.ui.dialogs.ProfilePictureDialogFragment;

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

        LinearLayout menuContainer = view.findViewById(R.id.menu_container);
        String[] menuItems = requireContext().getResources().getStringArray(R.array.profile_menu_items);

        for (int i = 0; i < menuItems.length; i++) {
            final int index = i;
            TextView item = new TextView(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 4, 0, 4);
            item.setLayoutParams(params);

            item.setText(menuItems[i]);
            item.setTextSize(16);
            item.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black));
            item.setPadding(16, 20, 16, 20);
            item.setBackgroundResource(R.drawable.item_ripple_background);
            item.setClickable(true);
            item.setFocusable(true);

            item.setOnClickListener(v -> {
                if (index == 0) {
                    startActivity(new Intent(getActivity(), EditInformationActivity.class));
                }
                // Add more cases if needed
            });

            menuContainer.addView(item);
        }

        TextView logout = view.findViewById(R.id.tv_logout);
        logout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
        });

        ImageButton btnAddAvatar = view.findViewById(R.id.btn_add_avatar);
        btnAddAvatar.setOnClickListener(v -> {
            ProfilePictureDialogFragment dialog = new ProfilePictureDialogFragment();
            dialog.setListener(new ProfilePictureDialogFragment.Listener() {
                @Override
                public void onChangePicture() {
                    dialog.showChooseChange(); // chuyển sang trạng thái chọn ảnh
                }

                @Override
                public void onDeletePicture() {
                    dialog.showConfirmDelete(); // chuyển sang xác nhận xóa
                }

                @Override
                public void onChooseFromGallery() {
                    Toast.makeText(requireContext(), "Gallery clicked", Toast.LENGTH_SHORT).show();
                    // sau khi chọn ảnh → gọi confirm
                    dialog.showConfirmChange();
                }

                @Override
                public void onTakePicture() {
                    Toast.makeText(requireContext(), "Camera clicked", Toast.LENGTH_SHORT).show();
                    // sau khi chụp ảnh → gọi confirm
                    dialog.showConfirmChange();
                }

                @Override
                public void onConfirmDelete() {
                    Toast.makeText(requireContext(), "Profile picture deleted", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onConfirmChange() {
                    Toast.makeText(requireContext(), "Profile picture changed", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show(getParentFragmentManager(), "ProfilePictureDialog");
        });

    }

}
