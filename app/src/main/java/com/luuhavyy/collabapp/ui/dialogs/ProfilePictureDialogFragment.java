package com.luuhavyy.collabapp.ui.dialogs;

import android.app.Dialog;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.luuhavyy.collabapp.R;

import lombok.Setter;

public class ProfilePictureDialogFragment extends DialogFragment {
    public interface Listener {
        void onChangePicture();

        void onDeletePicture();

        void onChooseFromGallery();

        void onTakePicture();

        void onConfirmDelete(Runnable onSuccess, Runnable onError);

        void onConfirmChange(Runnable onSuccess, Runnable onError);
    }

    private static final String ARG_IMAGE_URI = "arg_image_uri";
    @Setter
    private Listener listener;
    private Uri imageUri;
    private ImageView ivAvatar;

    private enum State {DEFAULT, CONFIRM_DELETE, CHOOSE_CHANGE, CONFIRM_CHANGE}

    private State currentState = State.DEFAULT;

    public void setImageUri(Uri uri) {
        imageUri = uri;
        if (ivAvatar != null) {
            ivAvatar.setImageURI(uri != null ? uri : null);
            if (uri == null) ivAvatar.setImageResource(R.drawable.avatar_placeholder);
        }
    }

    public static ProfilePictureDialogFragment newInstance(Uri imageUri) {
        ProfilePictureDialogFragment f = new ProfilePictureDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_IMAGE_URI, imageUri);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) imageUri = getArguments().getParcelable(ARG_IMAGE_URI);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_profile_picture, container, false);
        render((ViewGroup) root);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            int margin = 2 * getResources().getDimensionPixelSize(R.dimen.dialog_margin_horizontal);
            int width = Resources.getSystem().getDisplayMetrics().widthPixels - margin;
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.dialog_background));
        }
    }

    private void render(ViewGroup root) {
        root.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View layout;

        switch (currentState) {
            case DEFAULT:
                layout = renderDefaultState(inflater, root);
                break;
            case CHOOSE_CHANGE:
                layout = renderChooseChangeState(inflater, root);
                break;
            case CONFIRM_CHANGE:
                layout = renderConfirmChangeState(inflater, root);
                break;
            case CONFIRM_DELETE:
                layout = renderConfirmDeleteState(inflater, root);
                break;
            default:
                layout = new View(getContext());
        }

        ImageButton btnClose = layout.findViewById(R.id.btn_close);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dismiss());
        }
        root.addView(layout);
    }

    private View renderDefaultState(LayoutInflater inflater, ViewGroup root) {
        View layout = inflater.inflate(R.layout.dialog_profile_picture_default, root, false);
        layout.findViewById(R.id.btn_change_picture).setOnClickListener(v -> {
            if (listener != null) listener.onChangePicture();
        });

        View deleteBtn = layout.findViewById(R.id.btn_delete_picture);
        if (hasPicture()) {
            deleteBtn.setVisibility(View.VISIBLE);
            deleteBtn.setOnClickListener(v -> {
                if (listener != null) listener.onDeletePicture();
            });
        } else {
            deleteBtn.setVisibility(View.GONE);
        }

        ivAvatar = layout.findViewById(R.id.iv_avatar);
        setImageUri(imageUri);
        return layout;
    }

    private View renderChooseChangeState(LayoutInflater inflater, ViewGroup root) {
        View layout = inflater.inflate(R.layout.dialog_profile_picture_choose, root, false);
        layout.findViewById(R.id.btn_gallery).setOnClickListener(v -> {
            if (listener != null) listener.onChooseFromGallery();
        });
        layout.findViewById(R.id.btn_camera).setOnClickListener(v -> {
            if (listener != null) listener.onTakePicture();
        });
        ivAvatar = layout.findViewById(R.id.iv_avatar);
        setImageUri(imageUri);
        return layout;
    }

    private View renderConfirmChangeState(LayoutInflater inflater, ViewGroup root) {
        View layout = inflater.inflate(R.layout.dialog_profile_picture_confirm_change, root, false);
        ivAvatar = layout.findViewById(R.id.iv_avatar);
        setImageUri(imageUri);

        layout.findViewById(R.id.btn_confirm_change).setOnClickListener(v -> {
            if (listener != null) listener.onConfirmChange(() -> {
                // Success
                Toast.makeText(getContext(), "Changed profile picture successfully!", Toast.LENGTH_SHORT).show();
                currentState = State.DEFAULT;
                render(root);
                dismiss();
            }, () -> {
                // Error
                Toast.makeText(getContext(), "Failed to change profile picture!", Toast.LENGTH_SHORT).show();
            });
        });
        return layout;
    }

    private View renderConfirmDeleteState(LayoutInflater inflater, ViewGroup root) {
        View layout = inflater.inflate(R.layout.dialog_profile_picture_confirm_delete, root, false);
        layout.findViewById(R.id.btn_confirm_delete).setOnClickListener(v -> {
            if (listener != null) {
                listener.onConfirmDelete(
                        () -> {
                            dismiss();
                        },
                        () -> {
                            dismiss();
                        }
                );
            }
        });
        return layout;
    }

    public void showConfirmChange() {
        currentState = State.CONFIRM_CHANGE;
        render((ViewGroup) getView());
    }

    public void showConfirmDelete() {
        currentState = State.CONFIRM_DELETE;
        render((ViewGroup) getView());
    }

    public void showChooseChange() {
        currentState = State.CHOOSE_CHANGE;
        render((ViewGroup) getView());
    }

    private boolean hasPicture() {
        return imageUri != null;
    }
}