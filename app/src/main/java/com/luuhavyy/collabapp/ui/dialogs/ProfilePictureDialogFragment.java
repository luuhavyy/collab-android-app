package com.luuhavyy.collabapp.ui.dialogs;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.luuhavyy.collabapp.R;

public class ProfilePictureDialogFragment extends DialogFragment {

    public interface Listener {
        void onChangePicture();
        void onDeletePicture();
        void onChooseFromGallery();
        void onTakePicture();
        void onConfirmDelete();
        void onConfirmChange();
    }

    private Listener listener;
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private enum State { DEFAULT, CONFIRM_DELETE, CHOOSE_CHANGE, CONFIRM_CHANGE }
    private State currentState = State.DEFAULT;

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
            int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

            int margin = 2 * getResources().getDimensionPixelSize(R.dimen.dialog_margin_horizontal);
            // Calculate final width
            int width = screenWidth - margin;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;

            dialog.getWindow().setLayout(width, height);

            dialog.getWindow().setBackgroundDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.dialog_background)
            );
        }
    }

    private void render(ViewGroup root) {
        root.removeAllViews(); // Xóa layout cũ
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View layout;
        switch (currentState) {
            case DEFAULT:
                layout = inflater.inflate(R.layout.dialog_profile_picture_default, root, false);
                layout.findViewById(R.id.btn_change_picture).setOnClickListener(v -> {
                    if (listener != null) listener.onChangePicture();
                });
                if (hasPicture()) {
                    layout.findViewById(R.id.btn_delete_picture).setVisibility(View.VISIBLE);
                    layout.findViewById(R.id.btn_delete_picture).setOnClickListener(v -> {
                        if (listener != null) listener.onDeletePicture();
                    });
                }
                break;

            case CHOOSE_CHANGE:
                layout = inflater.inflate(R.layout.dialog_profile_picture_choose, root, false);
                layout.findViewById(R.id.btn_gallery).setOnClickListener(v -> {
                    if (listener != null) listener.onChooseFromGallery();
                });
                layout.findViewById(R.id.btn_camera).setOnClickListener(v -> {
                    if (listener != null) listener.onTakePicture();
                });
                break;

            case CONFIRM_CHANGE:
                layout = inflater.inflate(R.layout.dialog_profile_picture_confirm_change, root, false);
                layout.findViewById(R.id.btn_confirm_change).setOnClickListener(v -> {
                    if (listener != null) listener.onConfirmChange();
                    dismiss();
                });
                break;

            case CONFIRM_DELETE:
                layout = inflater.inflate(R.layout.dialog_profile_picture_confirm_delete, root, false);
                layout.findViewById(R.id.btn_confirm_delete).setOnClickListener(v -> {
                    if (listener != null) listener.onConfirmDelete();
                    dismiss();
                });
                break;

            default:
                layout = new View(getContext());
                break;
        }
        ImageButton btnClose = layout.findViewById(R.id.btn_close);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dismiss());
        }
        root.addView(layout);
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
        // TODO: kiểm tra nếu user đã có ảnh avatar
        return true;
    }
}
