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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.data.model.User;
import com.luuhavyy.collabapp.ui.activities.EditInformationActivity;
import com.luuhavyy.collabapp.ui.dialogs.ErrorDialog;
import com.luuhavyy.collabapp.ui.dialogs.ProfilePictureDialogFragment;
import com.luuhavyy.collabapp.ui.viewmodels.UserViewModel;
import com.luuhavyy.collabapp.utils.AuthUtil;
import com.luuhavyy.collabapp.utils.ImageUtil;
import com.luuhavyy.collabapp.utils.LoadingHandlerUtil;

import java.io.File;

public class ProfileFragment extends Fragment {
    private UserViewModel userViewModel;
    private Uri imageUri = null;
    private ProfilePictureDialogFragment dialog;
    private String userId = null;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::onGalleryResult);

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::onCameraResult);

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    granted -> {
                        if (granted) openCamera();
                        else
                            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                    });

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

        userId = AuthUtil.getCurrentUser().getUid();

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        LoadingHandlerUtil.executeOnceWithLoading(requireContext(), onLoaded -> {
            userViewModel.listenToUserRealtime(userId, onLoaded);  // start listening for changes
        });

        userViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                bindUserData(user, view);
            } else {
                ErrorDialog.show(requireContext(), "Failed to load user", () -> {
                });
            }
        });

        setupMenu(view);
        setupAvatarButton(view);
        setupLogoutButton(view);
    }

    private void setupMenu(View view) {
        LinearLayout menuContainer = view.findViewById(R.id.menu_container);
        String[] menuItems = getResources().getStringArray(R.array.profile_menu_items);
        menuContainer.removeAllViews();

        for (int i = 0; i < menuItems.length; i++) {
            menuContainer.addView(createMenuItem(menuItems[i], i));
        }
    }

    private void setupLogoutButton(View view) {
        view.findViewById(R.id.tv_logout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();

            BottomNavigationView nav = requireActivity().findViewById(R.id.bottom_nav);
            nav.setSelectedItemId(R.id.nav_home);
        });
    }

    private TextView createMenuItem(String text, int index) {
        TextView item = new TextView(requireContext());
        item.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        item.setText(text);
        item.setTextSize(16);
        item.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black));
        item.setPadding(16, 20, 16, 20);
        item.setBackgroundResource(R.drawable.item_ripple_background);
        item.setOnClickListener(v -> handleMenuClick(index));
        return item;
    }

    private void handleMenuClick(int index) {
        if (index == 0) {
            startActivity(new Intent(getActivity(), EditInformationActivity.class));
        }
        // Add more item here
    }

    private void setupAvatarButton(View view) {
        ImageButton btnAddAvatar = view.findViewById(R.id.btn_add_avatar);
        btnAddAvatar.setOnClickListener(v -> showProfilePictureDialog());
    }

    private void onGalleryResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            imageUri = result.getData().getData();
            showConfirmChangeDialog(imageUri);
        }
    }

    private void onCameraResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            showConfirmChangeDialog(imageUri);
        }
    }

    private void showProfilePictureDialog() {
        dialog = ProfilePictureDialogFragment.newInstance(imageUri);
        dialog.setListener(createDialogListener());
        dialog.show(getParentFragmentManager(), "ProfilePictureDialog");
    }

    private void bindUserData(User user, View view) {
        ((TextView) view.findViewById(R.id.tv_name)).setText(user.getName());
        ((TextView) view.findViewById(R.id.tv_email)).setText(user.getEmail());
        ((TextView) view.findViewById(R.id.tv_id)).setText(user.getUserid());

        ShapeableImageView avatar = view.findViewById(R.id.avatar);
        String base64 = user.getProfilepicture();

        if (base64 != null && !base64.isEmpty()) {
            imageUri = ImageUtil.saveBase64ImageToFile(requireContext(), base64);
            if (imageUri != null) avatar.setImageURI(imageUri);
            else avatar.setImageResource(R.drawable.avatar_placeholder);
        } else {
            avatar.setImageResource(R.drawable.avatar_placeholder);
        }

        if (dialog != null && dialog.isVisible()) dialog.setImageUri(imageUri);
    }

    private ProfilePictureDialogFragment.Listener createDialogListener() {
        return new ProfilePictureDialogFragment.Listener() {
            @Override
            public void onChangePicture() {
                dialog.showChooseChange();
            }

            @Override
            public void onDeletePicture() {
                dialog.showConfirmDelete();
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
            public void onConfirmDelete(Runnable onSuccess, Runnable onError) {
                Toast.makeText(requireContext(), "Profile picture deleted", Toast.LENGTH_SHORT).show();
                userViewModel.updateProfilePicture(requireContext(), null, userId, onSuccess, onError);
                // Add logic delete avatar
            }

            @Override
            public void onConfirmChange(Runnable onSuccess, Runnable onError) {
                if (imageUri != null) {
                    userViewModel.updateProfilePicture(requireContext(), imageUri, userId, onSuccess, onError);
                    Toast.makeText(requireContext(), "Profile picture changed", Toast.LENGTH_SHORT).show();
                } else {
                    onError.run();
                }
            }
        };
    }

    private void showConfirmChangeDialog(Uri uri) {
        if (dialog != null && dialog.isVisible()) {
            dialog.setImageUri(uri);
            dialog.showConfirmChange();
        } else {
            showProfilePictureDialog();
        }
    }

    private void openGallery() {
        galleryLauncher.launch(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
    }

    private void openCamera() {
        File file = createImageFile();
        if (file != null) {
            imageUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", file);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraLauncher.launch(intent);
        } else {
            Toast.makeText(requireContext(), "Cannot create image file", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() {
        File dir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return dir != null ? new File(dir, "avatar_" + System.currentTimeMillis() + ".jpg") : null;
    }

    private void checkAndRequestCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }
}
