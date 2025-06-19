package com.luuhavyy.collabapp.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.luuhavyy.collabapp.R;

import java.util.List;

import com.luuhavyy.collabapp.data.model.Promotion;

public class PromotionAdapter extends BaseAdapter {
    private Context context;
    private List<Promotion> promotions;
    private int selectedPosition = -1;

    public PromotionAdapter(Context context, List<Promotion> promotions) {
        this.context = context;
        this.promotions = promotions;
    }

    @Override
    public int getCount() {
        return promotions.size();
    }

    @Override
    public Object getItem(int position) {
        return promotions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_voucher_list, parent, false);
        }

        Promotion promotion = promotions.get(position);

        TextView txtDiscountType = convertView.findViewById(R.id.txtDiscountType);
        TextView txtDiscountInfor = convertView.findViewById(R.id.txtDiscountInfor);
        CheckBox btnChoosePromotion = convertView.findViewById(R.id.btnChoosePromotion);

        // Set discount type text
        if ("percentage".equals(promotion.getDiscounttype())) {
            txtDiscountType.setText(promotion.getDiscountvalue() + "%");
        } else {
            txtDiscountType.setText(String.format("%,d VNĐ", (long)promotion.getDiscountvalue()));
        }

        // Set discount info - FIXED VERSION
        String userId = promotion.getUserid();
        if (userId != null && !userId.isEmpty()) {
            txtDiscountInfor.setText("Voucher is only for you");
        } else {
            txtDiscountInfor.setText("");
        }

        btnChoosePromotion.setChecked(position == selectedPosition);
        btnChoosePromotion.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();
        });

        return convertView;
    }
    public Promotion getSelectedPromotion() {
        if (selectedPosition != -1) {
            return promotions.get(selectedPosition);
        }
        return null;
    }
}