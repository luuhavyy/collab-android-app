package com.luuhavyy.collabapp.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.slider.RangeSlider;
import com.google.android.material.tabs.TabLayout;
import com.luuhavyy.collabapp.R;
import com.luuhavyy.collabapp.data.model.ProductFilterSort;

import java.util.List;

public class DialogFilterAndSorting extends DialogFragment {
    public static DialogFilterAndSorting newInstance() {
        return new DialogFilterAndSorting();
    }

    public interface OnFilterApplyListener {
        void onFilterApplied(ProductFilterSort filterSort);
    }

    public void setOnFilterApplyListener(OnFilterApplyListener listener) {
        this.listener = listener;
    }

    private OnFilterApplyListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_filter_sorting, container, false);

        // Init views
        ImageButton btnClose = view.findViewById(R.id.btn_close);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        ViewFlipper viewFlipper = view.findViewById(R.id.view_flipper);
        RangeSlider priceSlider = view.findViewById(R.id.price_slider);
        TextView priceMin = view.findViewById(R.id.price_min);
        TextView priceMax = view.findViewById(R.id.price_max);
        CheckBox checkboxFrame = view.findViewById(R.id.checkbox_frame);
        CheckBox checkboxSunglasses = view.findViewById(R.id.checkbox_sunglasses);
        RadioGroup sortingGroup = view.findViewById(R.id.sorting_group);
        View btnReset = view.findViewById(R.id.btn_reset);
        View btnApply = view.findViewById(R.id.btn_apply);

        // Tab handling
        tabLayout.addTab(tabLayout.newTab().setText("Filter"));
        tabLayout.addTab(tabLayout.newTab().setText("Sorting"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewFlipper.setDisplayedChild(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        // Slider change
        priceSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            priceMin.setText("VND " + values.get(0).intValue());
            priceMax.setText("VND " + values.get(1).intValue());
        });

        // Close button
        btnClose.setOnClickListener(v -> dismiss());

        // Reset button
        btnReset.setOnClickListener(v -> {
            priceSlider.setValues(100000f, 1000000f);
            checkboxFrame.setChecked(false);
            checkboxSunglasses.setChecked(false);
            sortingGroup.clearCheck();
        });

        // Apply button
        btnApply.setOnClickListener(v -> {
            float min = priceSlider.getValues().get(0);
            float max = priceSlider.getValues().get(1);
            boolean frame = checkboxFrame.isChecked();
            boolean sun = checkboxSunglasses.isChecked();

            String sortBy = null;
            int checkedId = sortingGroup.getCheckedRadioButtonId();
            if (checkedId == R.id.sort_name_az) {
                sortBy = "name_asc";
            } else if (checkedId == R.id.sort_name_za) {
                sortBy = "name_desc";
            } else if (checkedId == R.id.sort_price_low) {
                sortBy = "price_asc";
            } else if (checkedId == R.id.sort_price_high) {
                sortBy = "price_desc";
            }

            if (listener != null) {
                listener.onFilterApplied(new ProductFilterSort(min, max, frame, sun, sortBy));
            }
            dismiss();
        });

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }
}
