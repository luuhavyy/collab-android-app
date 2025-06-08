package com.luuhavyy.collabapp.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.imageview.ShapeableImageView;
import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.data.model.User;
import com.luuhavyy.collabapp.ui.activities.EditInformationActivity;
import com.luuhavyy.collabapp.ui.dialogs.ErrorDialog;
import com.luuhavyy.collabapp.ui.dialogs.ProfilePictureDialogFragment;
import com.luuhavyy.collabapp.ui.viewmodels.UserViewModel;
import com.luuhavyy.collabapp.utils.ImageUtil;

import java.io.File;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProfileFragment extends Fragment {
    private ProfilePictureDialogFragment.Listener dialogListener;
    private ProfilePictureDialogFragment dialogInUse;
    private Uri imageUri = null;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private UserViewModel userViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        showConfirmChangeDialog(imageUri);
                    }
                }
        );

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        showConfirmChangeDialog(imageUri);
                    }
                }
        );
    }

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

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.fetchUserById("user005");

        userViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                bindUserData(user, view);
            } else {
                ErrorDialog.show(requireContext(), "Failed to load user", () -> {
                    userViewModel.fetchUserById("user005");
                });
            }
        });

        dialogListener = new ProfilePictureDialogFragment.Listener() {
            @Override
            public void onChangePicture() {
                if (dialogInUse != null) dialogInUse.showChooseChange();
            }

            @Override
            public void onDeletePicture() {
                if (dialogInUse != null) dialogInUse.showConfirmDelete();

            }

            @Override
            public void onChooseFromGallery() {
                openGallery();
            }

            @Override
            public void onTakePicture() {
                checkAndRequestCameraPermission();
            }

            @Override
            public void onConfirmDelete() {
                Toast.makeText(requireContext(), "Profile picture deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onConfirmChange(Runnable onSuccess, Runnable onError) {
                if (imageUri != null) {
                    userViewModel.updateProfilePicture(requireContext(), imageUri, "user005", onSuccess, onError);
                } else {
                    onError.run();
                }
                Toast.makeText(requireContext(), "Profile picture changed", Toast.LENGTH_SHORT).show();
                dialogInUse = new ProfilePictureDialogFragment();
                dialogInUse.setImageUri(imageUri);
                dialogInUse.setListener(dialogListener);
                dialogInUse.show(getParentFragmentManager(), "ProfilePictureDialog");
            }
        };

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
            dialogInUse = ProfilePictureDialogFragment.newInstance(imageUri);
            dialogInUse.setListener(dialogListener);
            dialogInUse.show(getParentFragmentManager(), "ProfilePictureDialog");
        });
    }

    private void bindUserData(User user, View view) {
        TextView tvName = view.findViewById(R.id.tv_name);
        TextView tvEmail = view.findViewById(R.id.tv_email);
        TextView tvId = view.findViewById(R.id.tv_id);
        ShapeableImageView avatarImage = view.findViewById(R.id.avatar);

        tvName.setText(user.getName());
        tvEmail.setText(user.getEmail());
        tvId.setText(user.getUserid());

        String base64 = user.getProfilepicture();
        if (base64 != null && !base64.isEmpty()) {
            imageUri = ImageUtil.saveBase64ImageToFile(requireContext(), base64);
            if (imageUri != null) {
                avatarImage.setImageURI(imageUri);

                if (dialogInUse != null && dialogInUse.isVisible()) {
                    dialogInUse.setImageUri(imageUri);
                }
            } else {
                avatarImage.setImageResource(R.drawable.avatar_placeholder);
            }
        } else {
            avatarImage.setImageResource(R.drawable.avatar_placeholder);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = createImageFile();
        if (photoFile != null) {
            imageUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraLauncher.launch(intent);
        } else {
            Toast.makeText(requireContext(), "Cannot create image file", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() {
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null) {
            return new File(storageDir, "avatar_" + System.currentTimeMillis() + ".jpg");
        } else {
            return null;
        }
    }

    private void showConfirmChangeDialog(Uri uri) {
        if (dialogInUse != null && dialogInUse.isVisible()) {
            dialogInUse.setImageUri(uri);
            dialogInUse.showConfirmChange();
        } else {
            dialogInUse = new ProfilePictureDialogFragment();
            dialogInUse.setImageUri(uri);
            dialogInUse.setListener(dialogListener);
            dialogInUse.show(getParentFragmentManager(), "ProfilePictureDialog");
        }
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    onPermissionGranted();
                } else {
                    onPermissionDenied();
                }
            });

    private void checkAndRequestCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void onPermissionGranted() {
        openCamera();
    }

    private void onPermissionDenied() {
        Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
    }
}